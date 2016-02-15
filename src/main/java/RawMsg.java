import org.apache.james.mime4j.stream.Field;

import java.util.List;

/**
 * Created by anders on 15/02/2016.
 */
public class RawMsg {
    List<Field> fields;
    String body;

    public RawMsg(String body, List<Field> fields) {
        this.body = body;
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getBody() {
        return body;
    }
}
