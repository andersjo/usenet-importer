import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    private static void addTrimmedParagraph(StringBuilder paragraph, List<String> paragraphs) {
        if (paragraph.length() > 0) {
            for (int i = paragraph.length() - 1; i >=0 && Character.isWhitespace(paragraph.charAt(i)); i--) {
                paragraph.deleteCharAt(i);
            }
        }

        String trimmedParagraph = paragraph.toString();
        if (trimmedParagraph.length() > 0) {
            paragraphs.add(trimmedParagraph);
        }
    }


    public static List<String> findParagraphs(String body) {
        List<String> paragraphs = new ArrayList<>();
        StringBuilder paragraph = new StringBuilder();

        for (String line : body.split("\n")) {
            boolean allSpace = line.matches("^\\s*$");

            if (allSpace || line.startsWith("\t") || line.startsWith(" ")) {
                addTrimmedParagraph(paragraph, paragraphs);
                paragraph.setLength(0);
                if (allSpace) {
                    continue;
                }
            }

            paragraph.append(line);
            paragraph.append("\n");
        }

        if (paragraph.length() > 0) {
            addTrimmedParagraph(paragraph, paragraphs);
        }

        return paragraphs;
    }


    public static String stripQuotes(String text) {
        StringBuilder out = new StringBuilder();

        boolean prevIsQuote = false;
        boolean currentIsQuote;

        for (String line : text.split("\n")) {
            currentIsQuote = line.startsWith(">") || line.startsWith("\t");

            if (prevIsQuote && !currentIsQuote) {
                // If the unquoted line is short, it may be a hanging word from the quote that would not
                // fit inside the quote, because lines are usually kept at a maximum of 76 characters.
                // If the current line has no spaces (= contains a single token), reclassify it as quoted
                if (!line.contains(" ")) {
                    currentIsQuote = true;
                }
            }

            if (prevIsQuote && !currentIsQuote && out.length() > 0) {
                out.append("\n");
            }

            if (!currentIsQuote) {
                out.append(line);
                out.append("\n");
            }
            prevIsQuote = currentIsQuote;
        }

        return out.toString();
    }
}
