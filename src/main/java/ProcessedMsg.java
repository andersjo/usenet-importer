import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessedMsg {
    public String docId;
    public String senderName;
    public String senderEmail;
    public String replyTo;
    public String subject;
    public String langCode;
    public String messageId;
    public List<String> newsgroups = new ArrayList<>();
    public List<String> paragraphs = new ArrayList<>();
    public Date date;

    public static final String[] columns = {"docId", "messageId", "senderName", "senderEmail", "subject", "langID",
            "newsgroups", "paragraphs", "date"};

    /**
     * empty constructor, elements are filled from outside. No setters (oooh!)
     */
    public ProcessedMsg() {}

    public boolean isValid() {
        return paragraphs.size() > 0
                && date != null
                && senderName != null
                && senderEmail != null
                && subject != null
                && langCode != null
                && messageId != null
                && newsgroups.size() > 0
                ;
    }


    public List<String> rowData() {
        List<String> elements = new ArrayList<>();

        elements.add(docId);
        elements.add(messageId);
        elements.add(senderName);
        elements.add(senderEmail);
        elements.add(subject);
        elements.add(langCode);
        elements.add(String.join(",", newsgroups));
        elements.add(String.join("\u2029", paragraphs));
        elements.add(date.toString());

        return elements;
    }

}
