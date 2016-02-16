import com.sun.org.apache.xpath.internal.operations.Mult;
import org.apache.james.mime4j.dom.*;
import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.dom.field.AddressListField;
import org.apache.james.mime4j.message.AbstractMessage;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.stream.Field;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class MsgProcessor {
    Set<Integer> paragraphHashes = new HashSet<>();
    List<String> unknownEncodings = new ArrayList<>();

    public MsgProcessor(){

    }

    public ProcessedMsg process(Message message) {
        ProcessedMsg processedMsg = new ProcessedMsg();
        // extractFields(msg, fields);
        System.out.println("=================");
        extractBody(message, processedMsg);


        return processedMsg;


    }

    private void extractBody(Message message, ProcessedMsg processedMessage) {
        String body = findFirstPlainBody(message, message.getCharset());
//        System.out.println("Body: " + body.substring(0, Math.min(100, body.length())));



//        List<String> stuff = new ArrayList<>();
    }


    /**
     * Finds the first plain text body part of the message and decodes that using
     * either the provided charset or the charset of an embedded part.
     *
     * In case the charset is invalided, the method falls back to a safe decoding.
     *
     * @return decoded string or null if no plain text part was found
     */
    private String findFirstPlainBody(Body body, String charset) {

        if (body instanceof SingleBody) {
            SingleBody singleBody = (SingleBody) body;
            return singleBodyToString(singleBody, charset);

        } else if (body instanceof Message) {
            Message messageBody = (Message) body;
            return findFirstPlainBody(messageBody.getBody(), messageBody.getCharset());

        } else if (body instanceof Multipart) {
            Multipart multipartBody = (Multipart) body;
            for (Entity entity : multipartBody.getBodyParts()) {
                if (entity.getMimeType().equals("text/plain")) {
                    return findFirstPlainBody(entity.getBody(), entity.getCharset());
                } else if (entity.isMultipart()) {
                    String foundBody = findFirstPlainBody(entity.getBody(), entity.getCharset());
                    if (foundBody != null) {
                        return foundBody;
                    }
                }
            }
            return null;
        } else {
            throw new RuntimeException("Invalid type of body encountered");
        }
    }

    private String singleBodyToString(SingleBody singleBody, String charset) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            singleBody.writeTo(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return output.toString(charset);
        } catch (UnsupportedEncodingException e) {
            unknownEncodings.add(charset);
            return output.toString();
        }
    }

    /**
     * this is where the magic happens
     * @param msg
     * @param fields
     * @return true iff we can process the whole thing
     * @throws DateTimeParseException if the date is not parseable
     */
    private boolean extractFields(ProcessedMsg msg, List<Field> fields) throws MessageProcessingError {
        for (Field field : fields) {
            System.out.println(field.getName() + ": " + field.getBody().substring(0, Math.min(field.getBody().length(), 25)));
            switch (field.getName()) {
                case "From":
                    if (field instanceof AddressListField) {
                        System.out.println("From IS AN AddressListField");
                        AddressListField addressListField = (AddressListField) field;
                        MailboxList addressList = addressListField.getAddressList().flatten();
                        if (addressList.size() > 0) {
                            Mailbox mailbox = addressList.get(0);
                            msg.senderEmail = mailbox.getAddress();
                            msg.senderName = mailbox.getName();
                        } else {
                            throw new MessageProcessingError("No valid From address found");
                        }
                    } else {
                        System.out.println("From but not an AddressListField");
                    }

                    break;
                case "Subject":
                    msg.subject = field.getBody();
                    break;
                case "Date":
//                    msg.date = extractDate(field.getBody());
                    break;
                case "Newsgroups":
                    msg.newsgroups = extractNewsgroups(field.getBody());
                    break;
                case "Reply-To":
                    break;



            }
        }

        return true;

    }

    private List<String> extractNewsgroups(String body) {
        return Arrays.asList(body.split("."));
    }

    /**
     * turn a string into a date with a certain format
     * @param dateString
     * @throws DateTimeParseException
     */
    private LocalDate extractDate(String dateString) throws MessageProcessingError {
        try {
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
            return date;
        }
        catch (DateTimeParseException exc) {
            String errorMessage = "'" + dateString + "' is not parseable as as date";
            throw new MessageProcessingError(errorMessage, exc);
        }
    }


}
