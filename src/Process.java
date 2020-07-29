import java.io.IOException;

public class Process implements Runnable{

    private int nodeId;
    private Parser parser;
    public static final String CLIENT = "Client";
    public static final String SERVER = "Server";
    private static Process instance = null;

    public static Process getInstance(int nodeId) throws IOException {
        if(instance == null){
            instance = new Process(nodeId);
        }
        return instance;
    }
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    private Process(int nodeId) throws IOException {
         parser = Parser.getInstance("config.txt");
         startThreads();
    }

    private void startThreads() {
        Thread server = new Thread(this);
        server.setName(this.SERVER);
        server.start();
        Thread client = new Thread(this);
        client.setName(this.CLIENT);
        client.start();
    }

    public void run() {
        String threadName = Thread.currentThread().getName();
        if(threadName.equals(this.SERVER)){
            
        }
        else{
            startClientThread();
        }

    }

    //The client thread needs to connect to every other process.
    private void startClientThread() {
    }

}
