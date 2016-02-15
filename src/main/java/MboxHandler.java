/**
 * Created by anders on 09/02/2016.
 */
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;

import static org.apache.james.mime4j.util.MimeUtil.unfold;


public class MboxHandler implements ContentHandler {
    int numMessages = 0;
    int numBodies = 0;

    public int getNumMessages() {
        return numMessages;
    }

    public int getNumBodies() {
        return numBodies;
    }


    @Override
    public void startMessage() throws MimeException {
        numMessages += 1;
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
//        System.out.println(rawField.getName());
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
        System.out.println("multipart:" + bd.getMimeType());
    }

    @Override
    public void endMultipart() throws MimeException {

    }

    @Override
    public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
        if (bd.getMimeType().equals("text/plain")) {


            processBody("");
            numBodies += 1;
        }


//        System.out.println("body: " + bd.getMimeType());
    }

    private void processBody(String body) {



    }

    @Override
    public void raw(InputStream is) throws MimeException, IOException {

    }
}
