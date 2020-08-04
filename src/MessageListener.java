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
            processMessage(m);
            Thread t = new Thread(this);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void processMessage(Message m) throws IOException {
        if(m instanceof RequestMessage){
            this.streams.getRequestQueue().add((RequestMessage) m);
            Message ack = new AckMessage("",this.nodeId);
            ack.setNodeId(this.nodeId);
            ack.setMessage("beepis");
            out.writeObject(ack);
        }
        else if(m instanceof PollingMessage){
            Message resp;
            if(!this.streams.getCriticalSectionQueue().isEmpty()){
                 resp = new PollResponseMessage(this.nodeId,true);
            }
            else{
                resp = new PollResponseMessage(this.nodeId,false);
            }
            out.writeObject(resp);
        }
        //received release. poll first from pqueue
        else if(m instanceof ReleaseMessage){
            RequestMessage req = streams.getRequestQueue().poll();
            System.out.println(req.getNodeId()  + " has been removed");
            Message response = new AckMessage("",this.nodeId);
            out.writeObject(response);
            evaluateTermination();
        }

    }

    private void evaluateTermination() {
        if(streams.getRequestQueue().isEmpty()){
            System.out.println(nodeId+"Finished, closing from message listener:");
            streams.terminateAllStreams();
            System.exit(0);
        }
    }
}
