import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class MessageListener implements Runnable{

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int nodeId;

    private Streams streams = Streams.getInstance();

    public MessageListener(ObjectInputStream input, ObjectOutputStream output, int nodeId){
        Thread t = new Thread(this);
        this.in = input;
        this.out = output;
        this.nodeId = nodeId;
        t.run();
    }

    //read object is blocking.
    //I need multiple reading at the same time foreach of objectinput stream.
    public void run(){
        try {
            Message m = (Message) in.readObject();
            System.out.println("message is from" + m.getNodeId());
            System.out.println("Is message instance of request?"+(m instanceof RequestMessage));
            this.streams.getRequestQueue().add((RequestMessage) m);
            Message ack = new AckMessage();
            ack.setNodeId(this.nodeId);
            ack.setMessage("beepis");
            out.writeObject(ack);
            Thread t = new Thread(this);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
