import java.util.Date;
import java.util.List;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class ProcessedMsg {

    public String senderName;
    public String senderEmail;
    public String subject;
    public List<String> newsgroups;
    public List<String> paragraphs;
    public Date date;

    public ProcessedMsg() {

    }

    public String toCSV(){
        String result = "";

        return result;
    }
}
