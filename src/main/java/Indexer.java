import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class Indexer {
    private final static String DEFAULT_FILES_DIRECTORY = "files";
    private final Directory directory;
    private final Analyzer analyzer;

    Indexer(Directory directory, Analyzer analyzer) {
        this.directory = directory;
        this.analyzer = analyzer;
    }

    Indexer(Initialize setup) {
        this(setup.getDirectory(), setup.getAnalyzer());
    }

    public static void main(String[] args) throws IOException {
        Initialize setup = new Initialize();
        Indexer indexer = new Indexer(setup);

        indexer.indexFolder(DEFAULT_FILES_DIRECTORY);
        System.out.println("Folder " + DEFAULT_FILES_DIRECTORY + " Indexed");
    }

    void deleteIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        indexWriter.deleteAll();
        indexWriter.close();
    }

    void indexFolder(String path) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        File folder = new File(path);
        File[] files = folder.listFiles();
        Parser parser = new Parser();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String fileContent = parser.parseFile(file.getPath());
                addDoc(indexWriter, fileName, fileContent);
            }
        }
        indexWriter.close();
    }

    private void addDoc(IndexWriter indexWriter, String title, String content) throws IOException {
        Document document = new Document();
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("content", content, Field.Store.YES));
        indexWriter.addDocument(document);
    }


    static class Parser {
        private final AutoDetectParser parser;
        private final BodyContentHandler handler;
        private final Metadata metadata;

        Parser() {
            this.parser = new AutoDetectParser();
            this.handler = new BodyContentHandler();
            this.metadata = new Metadata();
        }

        String parseFile(String filePath) {
            try (InputStream stream = TikaInputStream.get(Paths.get(filePath))) {
                parser.parse(stream, handler, metadata);
                return handler.toString();
            } catch (Exception ex) {
                ex.getStackTrace();
                System.out.println("Parsing failed for" + filePath);
                return "";
            }
        }
    }


}
