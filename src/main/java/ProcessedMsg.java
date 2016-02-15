import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class ProcessedMsg {

    public String senderName;
    public String senderEmail;
    public String msgID;
    public String sender;
    public String name;
    public String email;
    public String subject;
    public List<String> newsgroups;
    public List<String> paragraphs;
    public LocalDate date;

    public ProcessedMsg() {}


    public String toCSV(){
        StringBuilder result = new StringBuilder();
        //TODO: frickle all the fields together. Is there a CSV module?
        return result.toString();
    }
}
