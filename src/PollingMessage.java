import java.sql.Timestamp;

public class PollingMessage extends Message {
    public PollingMessage(String message, int nodeId, Timestamp timestamp) {
        super(message, nodeId, timestamp);
    }
}
