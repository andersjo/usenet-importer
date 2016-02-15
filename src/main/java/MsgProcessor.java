import org.apache.james.mime4j.stream.Field;

import java.util.Date;
import java.util.List;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class MsgProcessor {

    public MsgProcessor(){}


    public ProcessedMsg process(List<Field> fields, String bodyText) {
        ProcessedMsg msg = new ProcessedMsg();
        extractFields(msg, fields);





        return msg;

    }

    private boolean extractFields(ProcessedMsg msg, List<Field> fields) {
        for (Field field : fields) {
            switch (field.getName()) {
                case "From":
                    break;
                case "Subject":
                    break;
                case "Date":
                    break;
                case "Newsgroups":
                    break;
                case "Reply-To":
                    break;



            }
        }

        return true;

    }


}
