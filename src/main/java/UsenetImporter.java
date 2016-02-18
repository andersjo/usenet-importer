import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.apache.commons.cli.*;
import org.apache.james.mime4j.dom.Message;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

public class UsenetImporter {
    boolean headerWritten = false;
    final BufferedWriter outFile;
    ExecutorService executor;
    final HashingFilter hashingFilter = new HashingFilter();



    public UsenetImporter(String outputFile, int nThreads) throws IOException {
        Path outputPath = Paths.get(outputFile);
        outFile = Files.newBufferedWriter(outputPath);
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Options options = new Options();
        options.addOption("mboxDir", true, "Directory of Mbox files");
        options.addOption("outputCsv", true, "CSV file where output is written");
        options.addOption("nThreads", false, "Number of threads to use. Default 4");



        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        int nThreads = 4;
        if (cmd.hasOption("nThreads")) {
            nThreads = Integer.parseInt(cmd.getOptionValue("nThreads"));
        }

        System.err.println("Running importer script with " + nThreads + " threads");
        UsenetImporter importer = new UsenetImporter(cmd.getOptionValue("outputCsv"), nThreads);
        Path mboxFilesDir = FileSystems.getDefault().getPath(cmd.getOptionValue("mboxDir"));
        importer.importDir(mboxFilesDir);
        importer.close();
    }


    public void importDir(Path dir) throws IOException, InterruptedException {
        if (!headerWritten) writeCsvHeader();

        // Queue all files
        DirectoryStream<Path> mboxFiles = Files.newDirectoryStream(dir);
        List<SingleMboxImporter> importerCallables = new ArrayList<>();
        for (Path mboxFile : mboxFiles) {
            String pathName = mboxFile.toString();
            if (pathName.endsWith(".mbox") || pathName.endsWith(".mbox.gz")) {
                importerCallables.add(new SingleMboxImporter(mboxFile));
            }
        }

        for (Future<Boolean> future : executor.invokeAll(importerCallables)) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    void close() throws IOException {
        executor.shutdown();
        outFile.close();
    }


    private void writeCsvHeader() throws IOException {
        outFile.write(ProcessedMsg.csvHeader());
        outFile.newLine();
        headerWritten = true;
    }

    private class SingleMboxImporter implements Callable<Boolean> {
        Path mboxFile;

        public SingleMboxImporter(Path mboxFile) {
            this.mboxFile = mboxFile;
        }

        @Override
        public Boolean call() throws Exception {
            MsgProcessor msgProcessor = setupMsgProcessor();
            System.out.println("Importing " + mboxFile.getFileName());
            MboxMessages messages = new MboxMessages(openInputStream());
            for (Message message : messages) {
                ProcessedMsg processedMessage = msgProcessor.process(message);
                if (processedMessage.isValid()) {
                    synchronized (outFile) {
                        outFile.write(processedMessage.csvRow());
                        outFile.newLine();
                    }
                }
            }
            return true;
        }

        private MsgProcessor setupMsgProcessor() {
            MsgProcessor msgProcessor = new MsgProcessor();
            msgProcessor.addParagraphFilter(hashingFilter::isNew);
            msgProcessor.addParagraphFilter(p -> p.length() > 5);

            return msgProcessor;
        }

        private InputStream openInputStream() throws IOException {
            InputStream mboxInstream = Files.newInputStream(mboxFile);
            String pathName = mboxFile.toString();
            if (pathName.endsWith(".gz")) {
                mboxInstream = new GZIPInputStream(mboxInstream);
            }
            return mboxInstream;

        }
    }
}
