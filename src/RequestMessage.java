import java.sql.Timestamp;

public class RequestMessage extends Message{
    private Timestamp timestamp;

    public RequestMessage(String message, int nodeId, Timestamp timestamp) {
        super(message, nodeId, timestamp);
    }

}
