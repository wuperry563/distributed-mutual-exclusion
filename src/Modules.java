import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
        RequestMessage request = new RequestMessage("",this.nodeId);
        this.streams.getRequestQueue().add(request);
        //TODO: implement/utilize Num requests, inter request delay
        sendMessageToAllNodes(request);
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
        System.out.println(this.streams.getRequestQueue().peek().getNodeId()+"Is in my request queue");
        System.out.println(this.nodeId+"can now execute");
        executeCriticalSection();
    }

    private List<Message> sendMessageToAllNodes(Message message) {
        List<Message> messages = new ArrayList<>();
       Parser.nodes.forEach((k,v)->{
           if(k != this.nodeId){
               ObjectInputStream in = streams.getClientInputStreams().get(k);
               ObjectOutputStream out = streams.getClientOutputStreams().get(k);
               try {
                   out.writeObject(message);
                   Message m = (Message)in.readObject();
                   messages.add(m);
               } catch (IOException | ClassNotFoundException e) {
                   e.printStackTrace();
               }
           }
       });
       return messages;
    }

    //TODO: use cs-execution time, thread sleep that.
    //TODO: have a test here to ensure only one crit at a time?
    //TODO: then remove own request from queue
    //TODO: then send release messages to everyone.
    private void executeCriticalSection() {
        try {
            assertIsOnlyCriticalSection();
            System.out.println(nodeId+" adding locking critical section");
            NodeInfo info = parser.nodes.get(this.nodeId);
            this.streams.getCriticalSectionQueue().add(info);
            Thread.sleep(parser.meanCS);
            //finish executing
            System.out.println(nodeId+" unlocking critical section");
            this.streams.getCriticalSectionQueue().clear();
            this.streams.getRequestQueue().poll();
            Message m = new ReleaseMessage("",this.nodeId);
            this.sendMessageToAllNodes(m);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //make sure is first request, poll other servers to see if it's the only request?
    private void assertIsOnlyCriticalSection() throws Exception{
        Message poll = new PollingMessage("lmao",this.nodeId);
        List<Message> responses = sendMessageToAllNodes(poll);
        for(Message m : responses){
            PollResponseMessage resp = (PollResponseMessage)m;
            if(resp.isExecutingCritical()){
                throw new Exception("FATAL: " +resp.getNodeId() + " IS ALSO EXECUTING CRITICAL SECTION");
            }
        }

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
        RequestMessage r1 = new RequestMessage("",1);
        RequestMessage r2 = new RequestMessage("",2);
        RequestMessage r3 = new RequestMessage("",1);
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
