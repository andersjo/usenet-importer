import java.util.stream.Stream;

/**
 * Created by anders on 17/02/2016.
 */
public class StreamExperiment {
    public static void main(String[] args) {
        Stream.Builder<String> builder = Stream.<String>builder();
        builder.add("abe");
        builder.add("hej");
        builder.build();
    }
}
