import org.apache.james.mime4j.dom.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UsenetImporter
{
    public static void main(String[] args) throws IOException {
        MboxMessages messages = new MboxMessages("/users/anders/downloads/alt.animals.goats.mbox");
        Stream<Message> rawMsgStream = StreamSupport.stream(messages.spliterator(), false);

        rawMsgStream.limit(100).forEach(m -> System.out.println(m.getSubject()));

//        final MsgProcessor msgProcessor = new MsgProcessor();

//        rawMsgStream.map(msgProcessor::process).limit(2)
//                .forEach(m -> System.out.println(m.senderName));






    }
}
