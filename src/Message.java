import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    private String message;
    private int nodeId;
    private Timestamp timestamp;

    public Message(String message, int nodeId) {
        this.message = message;
        this.nodeId = nodeId;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    private Message(){
    }

    public Timestamp getTimestamp() {
        return timestamp;
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
