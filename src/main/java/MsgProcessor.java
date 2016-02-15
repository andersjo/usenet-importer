import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.dom.field.AddressListField;
import org.apache.james.mime4j.stream.Field;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class MsgProcessor {
    Set<Integer> paragraphHashes = new HashSet<>();

    public MsgProcessor(){

    }



    /**
     * process the fields from the Stream and the text, and parse it into a ProcessedMsg object
     * @param fields
     * @param bodyText
     * @return ProcessedMsg object
     */
    public ProcessedMsg process(List<Field> fields, String bodyText) {
        ProcessedMsg msg = new ProcessedMsg();
        try {
            extractFields(msg, fields);
            extractBody(bodyText);
        } catch (MessageProcessingError messageProcessingError) {
//            messageProcessingError.printStackTrace();
        }

        return msg;
    }

    public ProcessedMsg process(RawMsg rawMessage)  {
        System.out.println("=======");
        System.out.println(rawMessage.body);
        System.out.println("=======");
        return process(rawMessage.fields, rawMessage.body);
    }

    private void extractBody(String bodyText) {
        List<String> stuff = new ArrayList<>();

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
