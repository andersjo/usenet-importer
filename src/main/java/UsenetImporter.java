import org.apache.james.mime4j.dom.Message;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

public class UsenetImporter {
    boolean headerWritten = false;
    BufferedWriter outFile;

    public UsenetImporter(String outputFile) throws IOException {
        Path outputPath = Paths.get(outputFile);
        outFile = Files.newBufferedWriter(outputPath);
    }

    public static void main(String[] args) throws IOException {
        UsenetImporter importer = new UsenetImporter("/users/anders/downloads/me.csv");
//        importer.writeCsvRowsForFile("/users/anders/downloads/dk.videnskab.religion.mbox");

        Path mboxFilesDir = FileSystems.getDefault().getPath("/users/anders/downloads");
        importer.importDir(mboxFilesDir);
    }


    public void importDir(Path dir) throws IOException {
        if (!headerWritten) writeCsvHeader();

        DirectoryStream<Path> mboxFiles = Files.newDirectoryStream(dir);
        for (Path mboxFile : mboxFiles) {
            String pathName = mboxFile.toString();
            if (pathName.endsWith(".mbox") || pathName.endsWith(".mbox.gz")) {
                importFile(mboxFile);
            }
        }
    }

    public void importFile(Path mboxFile) throws IOException {
        if (!headerWritten) writeCsvHeader();

        InputStream mboxInstream = Files.newInputStream(mboxFile);
        String pathName = mboxFile.toString();
        if (pathName.endsWith(".gz")) {
            mboxInstream = new GZIPInputStream(mboxInstream);
        }
        writeCsvRowsForFile(mboxInstream);
    }

    private void writeCsvHeader() throws IOException {
        outFile.write(ProcessedMsg.csvHeader());
        outFile.newLine();
    }


    private void writeCsvRowsForFile(InputStream mbox) throws IOException {
        MboxMessages messages = new MboxMessages(mbox);
        Stream<Message> mimeMessageStream = StreamSupport.stream(messages.spliterator(), false);
        HashingFilter hashingFilter = new HashingFilter();

        final MsgProcessor msgProcessor = new MsgProcessor();
        msgProcessor.addParagraphFilter(hashingFilter::isNew);
        msgProcessor.addParagraphFilter(p -> p.length() > 5);

        mimeMessageStream.map(msgProcessor::process)
                .filter(ProcessedMsg::isValid)
                .forEach(m -> {
            try {
                outFile.write(m.csvRow());
                outFile.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
