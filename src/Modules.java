import java.io.IOException;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class Modules implements Runnable{
    private Streams streams;
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    private int nodeId;

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

    public Modules(Streams streams, int nodeId) {
        this.streams = streams;
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
        if(name.equals(TOP)){
            executeTopThread();
        }
        else{
            executeBottomThread();
        }
    }

    //Mutual Exclusion Service
    private void executeBottomThread() {

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
        System.out.println("I am node:"+this.nodeId+"this is the keyset for my client sockets:\n"+
                streams.getClientSockets().keySet());
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        Parser p = Parser.getInstance("config.txt");
        Streams streams = new Streams();
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

        Modules modules = new Modules(streams, 1);
    }
}
