import java.io.*;
import java.util.List;
import java.util.Random;

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

    public void run(){
        try {
            Message m = (Message) in.readObject();
            processMessage(m);
            Thread t = new Thread(this);
            t.run();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }


    }

    private void processMessage(Message m) throws IOException {
        if(m instanceof RequestMessage){
            this.streams.getRequestQueue().add((RequestMessage) m);
            Message ack = new AckMessage("",this.nodeId);
            ack.setNodeId(this.nodeId);
            ack.setMessage("beepis");
            streams.getMessageCount().incrementAndGet();
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
            streams.getMessageCount().incrementAndGet();
            out.writeObject(resp);
        }
        //received release. poll first from pqueue
        else if(m instanceof ReleaseMessage){
            RequestMessage req = streams.getRequestQueue().poll();
            System.out.println(req.getNodeId()  + " has been removed");
            Message response = new AckMessage("",this.nodeId);
            streams.getMessageCount().incrementAndGet();
            out.writeObject(response);
            evaluateTermination();
        }

    }

    private void evaluateTermination() {
        if(streams.getRequestQueue().isEmpty()){
            System.out.println(nodeId+"Finished, closing from message listener:");
            logTimesToFile();
            System.out.println("logging finished");
            streams.terminateAllStreams();
            System.exit(0);
        }
    }

    private void logTimesToFile() {
        FileWriter fileWriter;
        List<Long> times = streams.getTimes();
        try{
            fileWriter = new FileWriter("config-"+nodeId+".txt");
            FileWriter messageWriter = new FileWriter("messages-"+nodeId+".txt");
            messageWriter.write("Total Messages:\n"+streams.getMessageCount().get());
            for(Long time : times){
                try{
                    System.out.println("writing longs"+time);
                    fileWriter.write(time.toString());
                    fileWriter.write("\n");
                }
                catch(Exception e){
                    e.printStackTrace();
                    fileWriter.close();
                    messageWriter.close();
                    System.exit(-1);
                }
            }
            fileWriter.close();
            messageWriter.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }



    }
}
