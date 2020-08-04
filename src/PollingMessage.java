import java.sql.Timestamp;

public class PollingMessage extends Message {
    public PollingMessage(String message, int nodeId) {
        super(message, nodeId);
    }
}
