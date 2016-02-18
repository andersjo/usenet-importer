import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    static final CharBuffer INVALID_CHARS = CharBuffer.wrap(new char[]{239, 191, 189});
    static final CharBuffer NULL_CHARS = CharBuffer.wrap(new char[]{0});

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

    public static String replaceInvalidChar(String text) {
        return text.replace(INVALID_CHARS, NULL_CHARS);

    }

    /**
     * The providers of the newsgroup dump accidentally replaced
     * all characters outside of the standard ascii range with
     * the (signed) byte sequence 239 191 189.
     *
     * Every time we encounter such as sequence in the raw and undecoded
     * data, it is replaced with a single NULL byte (\0).

     */
    static ByteArrayOutputStream replaceSpecialCharBytesWithNullByte(byte[] text) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int lastFound = 0;
        for (int i = 0; i < text.length - 2; i++) {
            if (text[i] == (byte) 239 && text[i+1] == (byte) 191 && text[i+1] == (byte) 189) {
                if (i > 0)
                    output.write(text, lastFound, i - lastFound - 1);
                output.write(0);
                // Skip over the last part of the byte sequence
                i += 2;
                lastFound = i + 1;
            }
        }

        if (lastFound < text.length) {
            output.write(text, lastFound, text.length - lastFound);
        }

        return output;

    }


    public static boolean isQuoteHeader(String txt){
        String re = "(^[OI]n.+@.+:$)";
        Pattern p = Pattern.compile(re, Pattern.DOTALL);
        Matcher m = p.matcher(txt);

        return m.find() && txt.lastIndexOf('\n') == txt.indexOf('\n');

    }


    public static boolean isSPAM(String pieceOfSuspiciousLookingMeat){
        // TODO
        return false;
    }

}
