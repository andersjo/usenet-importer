/**
 * Created by anders on 09/02/2016.
 */
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MboxContentHandler implements ContentHandler {
    String body = "";
    List<Field> rawFields = new ArrayList<>();

    @Override
    public void startMessage() throws MimeException {
        body = "";
        rawFields.clear();
    }

    @Override
    public void endMessage() throws MimeException {

    }

    @Override
    public void startBodyPart() throws MimeException {

    }

    @Override
    public void endBodyPart() throws MimeException {

    }

    @Override
    public void startHeader() throws MimeException {

    }

    @Override
    public void field(Field rawField) throws MimeException {
        rawFields.add(rawField);
    }

    @Override
    public void endHeader() throws MimeException {

    }

    @Override
    public void preamble(InputStream is) throws MimeException, IOException {

    }

    @Override
    public void epilogue(InputStream is) throws MimeException, IOException {

    }

    @Override
    public void startMultipart(BodyDescriptor bd) throws MimeException {

    }

    @Override
    public void endMultipart() throws MimeException {

    }

    @Override
    public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
        if (bd.getMimeType().equals("text/plain")) {
            body += is.toString();
        }
    }

    @Override
    public void raw(InputStream is) throws MimeException, IOException {

    }
}
