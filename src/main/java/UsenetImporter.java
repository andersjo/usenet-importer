import org.apache.james.mime4j.dom.Message;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UsenetImporter
{


    public static void main(String[] args) throws IOException {
        MboxMessages messages = new MboxMessages("/Users/dirkhovy/Downloads/alt.animals.goats.mbox");
        Stream<Message> mimeMessageStream = StreamSupport.stream(messages.spliterator(), false);

//        LangIdV3 langid = new LangIdV3();
//        langid.append("Hello this is a language which I speak");
//        DetectedLanguage detectedLanguage = langid.classify(false);
//        System.out.println("Got lang: " + detectedLanguage.getLangCode());
//
        HashingFilter hashingFilter = new HashingFilter();
//
        final MsgProcessor msgProcessor = new MsgProcessor();
        msgProcessor.addParagraphFilter(hashingFilter::isNew);
        msgProcessor.addParagraphFilter(p -> p.length() > 5);



//        mimeMessageStream.limit(200).map(msgProcessor::process).filter(m -> m.paragraphs.size() > 0)
//                .forEach(m -> System.out.println("PARA: " + m.paragraphs.get(0)));

//        mimeMessageStream.map(m -> m.getSubject()).limit(10)
//                .map(String::toUpperCase)
//                .collect(Collectors.toList());

//        System.out.println(mimeMessageStream.limit(500).map(msgProcessor::process)
//                .filter(m -> m.paragraphs.size() > 0)
//                .collect(Collectors.groupingBy(m -> m.langCode, Collectors.counting())));


        List<ProcessedMsg> processed = mimeMessageStream.limit(1000).map(msgProcessor::process)
                .filter(m -> m.paragraphs.size() > 0)
                .filter(m -> m.paragraphs.get(0).startsWith("On "))
                .collect(Collectors.toList());

        for (ProcessedMsg msg : processed) {
//            System.out.println("===PARAGRAPH BEGIN===");
            System.out.println(msg.paragraphs.get(0));
//            System.out.println("===PARAGRAPH END===");
        }




//        rawMsgStream.map(msgProcessor::process).limit(2)
//                .forEach(m -> System.out.println(m.senderName));






    }
}
