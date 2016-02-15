import java.util.Date;
import java.util.List;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class MsgProcessor {

    public MsgProcessor(){}


    public ProcessedMsg process(String sender, String name, String email, String subject, List<String> newsgroups, List<String> paragraphs, Date date){
        ProcessedMsg processedMsg = new ProcessedMsg(sender, name, email, subject, newsgroups, paragraphs, date);
        return processedMsg;
    }
}
