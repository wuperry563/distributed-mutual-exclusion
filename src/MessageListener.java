import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

public class MessageListener implements Runnable{

    private ObjectInputStream in;

    private Streams streams = Streams.getInstance();

    public MessageListener(ObjectInputStream input){
        Thread t = new Thread(this);
        this.in = input;
        t.run();
    }

    public void run(){
        try {
            Message m = (Message) in.readObject();
            System.out.println("message is from" + m.getNodeId());
            System.out.println("Is message instance of request?"+(m instanceof RequestMessage));
            this.streams.getRequestQueue().add((RequestMessage) m);
            Thread t = new Thread(this);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
