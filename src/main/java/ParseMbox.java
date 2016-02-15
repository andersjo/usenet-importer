import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.MimeConfig;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by anders on 09/02/2016.
 */
public class ParseMbox {

    public static void main(String[] args) throws IOException {
        new ParseMbox().run();
    }

    public void run() throws IOException {
        MboxHandler handler = new MboxHandler();
        MimeConfig config = new MimeConfig();
        MimeStreamParser parser = new MimeStreamParser(config);
        parser.setContentHandler(handler);
        parser.setContentDecoding(true);
        parser.setRecurse();

        InputStream instream = new FileInputStream("/users/anders/downloads/alt.sports.basketball.nba.mbox");
        Scanner scanner = new Scanner(instream);

        try {
            while (true) {
                String line = scanner.nextLine();
                if (line.startsWith("From ")) {
                    if (parseMessage(instream, parser)) {

                    } else {
                        break;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            /* empty */
        }

        System.err.println("Read " + handler.getNumMessages());
        System.err.println("Number of bodies " + handler.getNumBodies());

    }

    private boolean parseMessage(InputStream instream, MimeStreamParser parser) {
        try {
            instream.read();
            parser.parse(instream);
        } catch (MimeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


}
