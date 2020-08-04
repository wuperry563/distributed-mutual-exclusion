import java.sql.Timestamp;

public class RequestMessage extends Message{
    private Timestamp timestamp;

    public RequestMessage(String message, int nodeId, Timestamp timestamp) {
        super(message, nodeId);
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
