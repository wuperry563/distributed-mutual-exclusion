import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class Modules implements Runnable{
    private Streams streams;
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    private int nodeId;
    private Parser parser;

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public Streams getStreams() {
        return streams;
    }

    public void setStreams(Streams streams) {
        this.streams = streams;
    }

    public Modules(int nodeId) {
        try{
            parser = Parser.getInstance("");
        }
        catch(IOException e){
            e.printStackTrace();
        }

        this.streams = Streams.getInstance();
        this.nodeId = nodeId;
        Thread top = new Thread(this);
        top.setName(this.TOP);
        Thread bot = new Thread(this);
        bot.setName(this.BOTTOM);
        top.start();
        bot.start();
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println("node "+this.nodeId+" streams "+streams.getServerInputStreams().keySet());
        if(name.equals(TOP)){
            executeTopThread();
        }
        else{
            executeBottomThread();
        }
    }

    //Mutual Exclusion Service
    private void executeBottomThread() {
//        System.out.println("InputStreams");
//        System.out.println("node"+this.nodeId+""+streams.getServerInputStreams().keySet());
//        streams.getServerInputStreams().forEach((k,v)->{
//            if(k != this.nodeId){
//                System.out.println("thread started for  :" + k);
//                MessageListener listener = new MessageListener(v);
//            }
//        });
    }

    //Application
    private void executeTopThread() {
        System.out.println("top");
        //generate crit section requests and execute on receiving permission.
        csEnter();
    }

    private void csEnter() {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        RequestMessage request = new RequestMessage("",this.nodeId, t);
        this.streams.getRequestQueue().add(request);
        //TODO: implement/utilize Num requests, inter request delay
        sendRequestToAllNodes(request);
        attemptExecution();
    }

    private void attemptExecution() {
        boolean canExecute = canExecuteCriticalSection();
        while(!canExecute) {
            canExecute = canExecuteCriticalSection();
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //can now execute?
        System.out.println(this.nodeId+"can now execute");
        executeCriticalSection();
    }

    private void sendRequestToAllNodes(RequestMessage requestMessage) {
       Parser.nodes.forEach((k,v)->{
           if(k != this.nodeId){
               ObjectInputStream in = streams.getClientInputStreams().get(k);
               ObjectOutputStream out = streams.getClientOutputStreams().get(k);
               try {
                   out.writeObject(requestMessage);
                   Message m = (Message)in.readObject();
                   System.out.println(this.nodeId+"Read Message from server"+m.getMessage());
               } catch (IOException | ClassNotFoundException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    //TODO: use cs-execution time, thread sleep that.
    //TODO: have a test here to ensure only one crit at a time?
    //TODO: then remove own request from queue
    //TODO: then send release messages to everyone.
    private void executeCriticalSection() {
        try {
            assertIsOnlyCriticalSection();
            Thread.sleep(parser.meanCS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //make sure is first request, poll other servers to see if it's the only request?
    private void assertIsOnlyCriticalSection() {
        Message poll = new PollingMessage("lmao",this.nodeId,new Timestamp(System.currentTimeMillis()));
        Parser.nodes.forEach((k,v)->{
            if(k != this.nodeId){
                ObjectInputStream in = streams.getClientInputStreams().get(k);
                ObjectOutputStream out = streams.getClientOutputStreams().get(k);
                try {
                    out.writeObject(poll);
                    PollResponseMessage m = (PollResponseMessage)in.readObject();
                    if(m.isExecutingCritical()){
                        System.out.println("VIOLATES TEST: NODE ID"+m.getNodeId()+" IS ALSO EXECUTING CRITICAL SECTION");
                    }
                    System.out.println(this.nodeId+"Read Message from server"+m.getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean canExecuteCriticalSection() {
        return this.streams.getRequestQueue().peek().getNodeId() == this.nodeId;
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        Parser p = Parser.getInstance("config.txt");
        Streams streams = Streams.getInstance();
        streams.getServerInputStreams();
        streams.getServerOutputStreams();
        streams.getClientOutputStreams();
        streams.getClientInputStreams();

        Timestamp t = new Timestamp(System.currentTimeMillis());
        Thread.sleep(10);
        Timestamp t2 = new Timestamp(System.currentTimeMillis());
        RequestMessage r1 = new RequestMessage("",1,t);
        RequestMessage r2 = new RequestMessage("",2, t);
        RequestMessage r3 = new RequestMessage("",1, t2);
        RequestMessageComparator comparator = new RequestMessageComparator();
        Queue<RequestMessage> requestQueue = new PriorityBlockingQueue<>(16,comparator);
        requestQueue.add(r1);
        requestQueue.add(r2);
        requestQueue.add(r3);
        System.out.println("l;mao");
        System.out.println(requestQueue.poll().getNodeId());
        System.out.println(requestQueue.poll().getNodeId());
        System.out.println(requestQueue.poll().getNodeId());

        Modules modules = new Modules(1);
    }
}
