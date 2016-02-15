import java.util.ArrayList;
import java.util.List;

/**
 * Created by anders on 15/02/2016.
 */
public class TextUtil {

    public static String[] findParagraphs(String body, boolean stripQuotedText) {
        /**
         * A paragraph ends if
         * - the last symbol on the line is a final punctuation character: .!? (Perhaps include smiley)
         * - the next line is empty.
         * - the next line changes quote or indent status
         */
        String[] out = {"a"};
        return out;
    }

    private static int firstNonQuoteCharPosition(String line) {
        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);
            if (currentChar != '\t' && currentChar != '>') {
                return i;
            }
        }
        return line.length();
    }


    public static List<String> stripQuotes(List<String> lines) {
        boolean prevIsQuote = false;
        boolean currentIsQuote = false;
        String prevLine = null;

        List<String> linesOut = new ArrayList<String>();

        for (String line : lines) {
            currentIsQuote = line.startsWith(">") || line.startsWith("\t");
            if (prevIsQuote && !currentIsQuote) {
                // If the unquoted line is short, it may be a hanging word from the quote that would not
                // fit inside the quote, because lines are usually kept at a maximum of 76 characters.
                // If this is true, reclassify the current line as quoted
                int quoteIndent = firstNonQuoteCharPosition(prevLine);

                // Could current line fit on previous line if quotes were removed?
                if (prevLine.length() - quoteIndent + line.length() <= 76) {
                    currentIsQuote = true;
                }

            }

            prevIsQuote = currentIsQuote;
            prevLine = line;
        }


        return linesOut;

    }
}
