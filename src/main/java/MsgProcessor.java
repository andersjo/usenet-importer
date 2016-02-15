import org.apache.james.mime4j.stream.Field;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Created by dirkhovy on 2/15/16.
 */
public class MsgProcessor {

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
        extractFields(msg, fields);

        return msg;

    }

    /**
     * this is where the magic happens
     * @param msg
     * @param fields
     * @return true iff we can process the whole thing
     * @throws DateTimeParseException if the date is not parseable
     */
    private boolean extractFields(ProcessedMsg msg, List<Field> fields) throws DateTimeParseException{
        for (Field field : fields) {
            switch (field.getName()) {
                case "From":
                    break;
                case "Subject":
                    break;
                case "Date":
                    msg.date = extractDate(field.getBody());
                case "Newsgroups":
                    break;
                case "Reply-To":
                    break;

            }
        }

        return true;

    }

    /**
     * turn a string into a date with a certain format
     * @param dateString
     * @throws DateTimeParseException
     */
    private LocalDate extractDate(String dateString) throws DateTimeParseException{
        try {
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
            return date;
        }
        catch (DateTimeParseException exc) {
            System.out.printf("%s is not parsable!%n", dateString);
            throw exc;      // Rethrow the exception.
        }
    }


}
