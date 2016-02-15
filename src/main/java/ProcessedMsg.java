import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class ProcessedMsg {
    private static final String SEPARATOR = "\t";
    private static final String ENCAPSULATOR = "\t";

    public String msgID;
    public String senderName;
    public String senderEmail;
    public String subject;
    public String langID;
    public List<String> newsgroups;
    public List<String> paragraphs;
    public LocalDate date;

    /**
     * empty constructor, elements are filled from outside. No setters (oooh!)
     */
    public ProcessedMsg() {}


    /**
     *
     * @return
     */
    public String toCSV(){
        StringJoiner elements = new StringJoiner(SEPARATOR);
        elements.add(msgID);
        elements.add(senderName);
        elements.add(senderEmail);
        elements.add(subject);
        elements.add(langID);
        elements.add(concatenate(newsgroups));
        elements.add(concatenate(paragraphs));
        elements.add(date.toString());

        return elements.toString();
    }

    //TODO: do we need a header() function to get the names of the CSV fields

    /**
     * join a list with SEPARATOR and encapsulate it with ENCAPUSLATOR
     * @param someList
     * @return
     */
    private String concatenate(List<String> someList){
        StringJoiner elements = new StringJoiner(SEPARATOR, ENCAPSULATOR, ENCAPSULATOR);

        for (String element: someList){
            elements.add(element);
        }

        // TODO: escape ENCAPSULATOR string in elements!
        return elements.toString();

    }
}
