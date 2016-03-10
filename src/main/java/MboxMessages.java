import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.MimeConfig;

import java.io.*;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MboxMessages implements Iterator<Message>, Iterable<Message> {
    BufferedInputStream instream;
    MboxContentHandler handler;

    DefaultMessageBuilder messageBuilder = new DefaultMessageBuilder();

    Message nextMessage = null;
    ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();

    int numCalls = 0;


    public MboxMessages(InputStream mbox) throws IOException {
        MimeConfig mimeConfig = new MimeConfig();
        mimeConfig.setMaxLineLen(10_000);
        messageBuilder.setMimeEntityConfig(mimeConfig);

        instream = new BufferedInputStream(mbox);
        // Read a single byte to prevent matching "From" on the first line
        instream.read();
        _fetchNext();
    }

    private void _fetchMessageBytes() throws IOException {
        numCalls++;
        messageBytes.reset();
        byte[] lineBytes = new byte[16384];

        while (true) {
            // Copy one line a time into the `messageBytes` buffer
            for (int i = 0; i < lineBytes.length; i++) {
                int readChar = instream.read();
                if (readChar == -1) {
                    messageBytes.write(lineBytes, 0, i);
                    return;
                }

                lineBytes[i] = (byte) readChar;

                if (readChar == '\n') {
                    // Check that beginning of line matches 'From'
                    if (matchesFromPattern(lineBytes, i + 1)) {
                        // We have a whole message in `messageBytes`
                        return;
                    } else {
                        messageBytes.write(lineBytes, 0, i + 1);
                        break;
                    }
                }
            }
        }
    }

    private boolean matchesFromPattern(byte[] line, int length) {
        // Does the start of the line match the following pattern:
        //  From 999554671203986290
        //  From -9041807362097321792
        // i.e. the string 'From', a space, an optional minus sign, and a bunch of numbers eventually terminating in a newline
        boolean startsWithFrom = line[0] == 'F' && line[1] == 'r' && line[2] == 'o' && line[3] == 'm' && line[4] == ' ';

        if (length > 7 && startsWithFrom && (Character.isDigit(line[5]) || line[5] == '-')) {
            // The rest of the characters must be digits
            int i = 6;
            for (; Character.isDigit(line[i]); i++) ;
            return line[i] == '\n';
        } else {
            return false;
        }
    }


    private void _fetchNext() throws IOException {
        nextMessage = null;
        while (nextMessage == null) {
            _fetchMessageBytes();
            if (messageBytes.size() > 0) {
                nextMessage = parseMessage(new ByteArrayInputStream(messageBytes.toByteArray()));
            } else {
                return;
            }
        }
    }

    private Message parseMessage(InputStream messageInstream) {
        try {
            return messageBuilder.parseMessage(messageInstream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Ignore a parsing error that happens inside of the Apache mime parser.
            if (!e.getStackTrace()[0].getClassName().equals("org.apache.james.mime4j.io.MimeBoundaryInputStream")) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return nextMessage != null;
    }

    @Override
    public Message next() {
        try {
            return nextMessage;
        } finally {
            try {
                _fetchNext();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Iterator<Message> iterator() {
        return this;
    }

    public static void main(String[] args) throws IOException {
//        MboxMessages messages = new MboxMessages("/users/anders/downloads/alt.sports.basketball.nba.mbox");
//        Stream<Message> msgStream = StreamSupport.stream(messages.spliterator(), false);
//
//        System.out.println(msgStream.count());
//        System.out.println("NumCalls " + messages.numCalls);
    }

}
