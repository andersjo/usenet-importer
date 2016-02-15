import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class ProcessedMsg {

    public String sender;
    public String name;
    public String email;
    public String subject;
    public List<String> newsgroups;
    public List<String> paragraphs;
    public Date date;

    public ProcessedMsg(String sender, String name, String email, String subject, List<String> newsgroups, List<String> paragraphs, Date date){
        this.sender = sender;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.newsgroups = newsgroups;
        this.paragraphs = paragraphs;
        this.date = date;
    }

    public String toCSV(){
        String result = "";

        return result;
    }
}
