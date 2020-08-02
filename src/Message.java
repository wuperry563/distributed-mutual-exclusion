import java.io.Serializable;

public class Message implements Serializable {
    private String message;
    private int nodeId;

    public Message(String message, int nodeId) {
        this.message = message;
        this.nodeId = nodeId;
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
