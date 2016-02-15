/**
 * Created by anders on 15/02/2016.
 */
public class MessageProcessingError extends Exception {
    public MessageProcessingError(String message) {
        super(message);
    }

    public MessageProcessingError(String message, Throwable cause) {
        super(message, cause);
    }
}
