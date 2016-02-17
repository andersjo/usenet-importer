import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class ProcessedMsg {
    private static final String SEPARATOR = "\t";
    private static final String ENCAPSULATOR = "\"";
    private static final String DOUBLE_ENCAPSULATOR = ENCAPSULATOR + ENCAPSULATOR;

    public String msgID;
    public String senderName;
    public String senderEmail;
    public String replyTo;
    public String subject;
    public String langCode;
    public String messageId;
    public List<String> newsgroups = new ArrayList<>();
    public List<String> paragraphs = new ArrayList<>();
    public Date date;

    /**
     * empty constructor, elements are filled from outside. No setters (oooh!)
     */
    public ProcessedMsg() {}

    public boolean isValid() {
        return paragraphs.size() >= 1;
    }




    /**
     *
     * @return a concatenation of the fields, joined by SEPARATOR
     */
    public String toCSV(){
        StringJoiner elements = new StringJoiner(SEPARATOR);

        // assemble fields
        //***************************
        //*******   WARNING:   ******
        //***************************
        // if you change the order or add fields,
        // change getCSVHeader() as well!

        elements.add(msgID);
        elements.add(senderName);
        elements.add(senderEmail);
        elements.add(subject);
        elements.add(langCode);
        elements.add(concatenate(newsgroups));
        elements.add(concatenate(paragraphs));
        elements.add(date.toString());

        return elements.toString();
    }

    public String getCSVHeader(){
        StringJoiner elements = new StringJoiner(SEPARATOR);

        // assemble fields
        //***************************
        //*******   WARNING:   ******
        //***************************
        // if you change the order or add fields,
        // change toCSV() as well!
        elements.add("msgID");
        elements.add("senderName");
        elements.add("senderEmail");
        elements.add("subject");
        elements.add("langID");
        elements.add("newsgroups");
        elements.add("paragraphs");
        elements.add("date");

        return elements.toString();
    }

    /**
     * join a list with SEPARATOR and encapsulate it with ENCAPSULATOR
     * @param someList of strings to concatenate
     * @return escaped and joined list
     */
    private static String concatenate(List<String> someList){
        StringJoiner elements = new StringJoiner(SEPARATOR, ENCAPSULATOR, ENCAPSULATOR);

        for (String element: someList){
            elements.add(element);
        }

        // replace the ENCAPSULATOR with a double version, after splitting up existing double versions
        return elements.toString().replaceAll(DOUBLE_ENCAPSULATOR, ENCAPSULATOR + " " + ENCAPSULATOR).replaceAll(ENCAPSULATOR, DOUBLE_ENCAPSULATOR);

    }
}
