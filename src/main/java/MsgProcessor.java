import cmu.arktweetnlp.Twokenize;
import com.carrotsearch.labs.langid.DetectedLanguage;
import com.carrotsearch.labs.langid.LangIdV3;
import org.apache.james.mime4j.dom.*;
import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.util.MimeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MsgProcessor {
    List<String> unknownEncodings = new ArrayList<>();
    List<Predicate<String>> paragraphFilters = new ArrayList<>();
    LangIdV3 langid = new LangIdV3();
    private Pattern threeReplacementCharsPat = Pattern.compile("�{3}");

    public MsgProcessor(){

    }

    public void addParagraphFilter(Predicate<String> function) {
        paragraphFilters.add(function);
    }

    public ProcessedMsg process(Message message) {
        ProcessedMsg processedMsg = new ProcessedMsg();
        try {
            String body = extractBody(message);
            body = threeReplacementCharsPat.matcher(body).replaceAll("�");
            List<String> paragraphs = TextUtil.findParagraphs(TextUtil.stripQuotes(body));

            paragraphs = applyParagraphFilters(paragraphs);
            processedMsg.langCode = detectLanguage(paragraphs);

            paragraphs = tokenize(paragraphs);
            processedMsg.paragraphs = paragraphs;
            extractFields(message, processedMsg);



        } catch (MessageProcessingError e) {
            return null;
        }

        return processedMsg;


    }

    private List<String> tokenize(List<String> paragraphs) {
        return paragraphs.stream()
                .map(MsgProcessor::twokenize)
                .collect(Collectors.toList());
    }

    private static String twokenize(String paragraph) {
        return Twokenize.tokenize(paragraph).stream()
                .collect(Collectors.joining(" "));
    }

    private String detectLanguage(List<String> paragraphs) {
        if (paragraphs.size() == 0)
            return "unknown";
        try {
            paragraphs.stream().forEach(langid::append);
            DetectedLanguage detectedLanguage = langid.classify(false);
            return detectedLanguage.getLangCode();
        } finally {
            langid.reset();
        }


    }

    private List<String> applyParagraphFilters(List<String> paragraphs) {
        Stream<String> paragraphStream = paragraphs.stream();
        for (Predicate<String> pred: paragraphFilters) {
            paragraphStream = paragraphStream.filter(pred);
        }

        return paragraphStream.collect(Collectors.toList());
    }

    private String extractBody(Message message) throws MessageProcessingError {
        return findFirstPlainBody(message, message.getCharset());
    }


    /**
     * Finds the first plain text body part of the message and decodes that using
     * either the provided charset or the charset of an embedded part.
     *
     * In case the charset is invalided, the method falls back to a safe decoding.
     *
     * @return decoded string or null if no plain text part was found
     */
    private String findFirstPlainBody(Body body, String charset) throws MessageProcessingError {

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
            throw new MessageProcessingError("No valid body found");
        } else {
            throw new MessageProcessingError("No valid body found");
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

    private void extractFields(Message message, ProcessedMsg processedMessage) throws MessageProcessingError {
        MailboxList fromList = message.getFrom();

        if (fromList != null && fromList.size() >= 1) {
            Mailbox from = fromList.get(0);
            processedMessage.senderName = from.getName();
            processedMessage.senderEmail = from.getAddress();
        }

        AddressList replyToList = message.getReplyTo();
        if (replyToList != null && replyToList.size() >= 1) {
            Address replyTo = message.getReplyTo().get(0);
            processedMessage.replyTo = replyTo.toString();
        }

        Mailbox sender = message.getSender();
        if (sender != null) {
            if (processedMessage.senderName == null)
                processedMessage.senderName = sender.getName();
            if (processedMessage.senderEmail == null)
                processedMessage.senderEmail = sender.getAddress();
        }

        processedMessage.subject = message.getSubject();
        processedMessage.date = message.getDate();

        processedMessage.messageId = message.getMessageId();
        processedMessage.newsgroups = extractNewsgroups(message);

    }

    private List<String> extractNewsgroups(Message message) {
        Header header = message.getHeader();
        List<Field> newsgroupFields = header.getFields("Newsgroups");
        if (newsgroupFields.size() >= 1) {
            Field newsgroupField = newsgroupFields.get(0);

            String newsgroupsList = MimeUtil.unfold(newsgroupField.getBody());
            return Arrays.asList(newsgroupsList.split(","));
        }

        return Collections.<String>emptyList();
    }

}
