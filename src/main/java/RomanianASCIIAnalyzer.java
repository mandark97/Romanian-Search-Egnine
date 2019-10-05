import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Scanner;

public final class RomanianASCIIAnalyzer extends StopwordAnalyzerBase {
    private final CharArraySet stemExclusionSet;

    private final static String DEFAULT_STOPWORD_FILE = "stop.txt";
    private static final String STOPWORDS_COMMENT = "#";

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        static {
            try {
                System.out.println(System.getProperty("user.dir"));

                DEFAULT_STOP_SET = loadStopwordSet(false, RomanianAnalyzer.class,
                        DEFAULT_STOPWORD_FILE, STOPWORDS_COMMENT);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }

    private static class CustomStopwords {
        static CharArraySet loadStopwords(String stopwordsPath) {
            CharArraySet customStopwords = CharArraySet.EMPTY_SET;
            String stopwords;

            try (Scanner scanner = new Scanner(Paths.get(stopwordsPath))) {
                stopwords = removeDiacritics(scanner.useDelimiter("\\A").next());
                customStopwords = WordlistLoader.getWordSet(new StringReader(stopwords));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return customStopwords;
        }

        private static String removeDiacritics(String stopwords) {
            return Normalizer.normalize(stopwords.toLowerCase(), Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        }
    }


    RomanianASCIIAnalyzer(String stopwordsPath) throws IOException {
        this(CustomStopwords.loadStopwords(stopwordsPath));
    }

    RomanianASCIIAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET);
    }

    RomanianASCIIAnalyzer(CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }

    RomanianASCIIAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new ClassicFilter(source);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopwords);
        result = new SnowballFilter(result, new RomanianStemmer());
        result = new ASCIIFoldingFilter(result);

        return new TokenStreamComponents(source, result);
    }
}