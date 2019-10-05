import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;


public class Initialize {
    private final static String STOPWORDS_FILE = "stop.txt";
    private final static String INDEX_PATH = "index";

    private final Analyzer analyzer;

    Analyzer getAnalyzer() {
        return analyzer;
    }

    Directory getDirectory() {
        return directory;
    }

    private final Directory directory;

    Initialize() throws IOException {
        this.analyzer = new RomanianASCIIAnalyzer(STOPWORDS_FILE);
        this.directory = new MMapDirectory(Paths.get(INDEX_PATH));
    }

    public static void main(String[] args) throws IOException, ParseException {
        String filesPath = "files";
        String query = "test test";

        Initialize setup = new Initialize();
        Indexer indexer = new Indexer(setup);
        indexer.deleteIndex();
        indexer.indexFolder(filesPath);

        Searcher searcher = new Searcher(setup);
        searcher.analyzeQuery(query);
        searcher.searchQuery(query);
    }
}
