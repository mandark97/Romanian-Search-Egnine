import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private final Analyzer analyzer;
    private final Directory directory;

    public static void main(String[] args) throws IOException, ParseException {
        Initialize setup = new Initialize();
        Searcher searcher = new Searcher(setup);

        searcher.searchQuery("călătorie");
    }

    Searcher(Directory directory, Analyzer analyzer) {
        this.directory = directory;
        this.analyzer = analyzer;
    }

    Searcher(Initialize setup) {
        this(setup.getDirectory(), setup.getAnalyzer());
    }

    void searchQuery(String queryString) throws IOException, ParseException {
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(queryString);
        analyzeQuery(queryString);

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs docs = searcher.search(query, 10);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + "hits");
        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document d = searcher.doc(docId);
            System.out.println(hit.score + "\t" + d.get("title"));
        }
        reader.close();
    }

    void analyzeQuery(String queryString) throws IOException {
        List<String> result = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream("ceva", queryString);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            result.add(attr.toString());
        }

        tokenStream.close();
        System.out.println(result);
    }
}
