import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    private String message;
    private int nodeId;
    private Timestamp timestamp;

    public Message(String message, int nodeId, Timestamp timestamp) {
        this.message = message;
        this.nodeId = nodeId;
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Message() {
        this.message = "";
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
