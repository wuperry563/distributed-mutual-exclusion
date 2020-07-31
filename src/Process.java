import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class Process implements Runnable{

    private int nodeId;
    private Parser parser;
    private NodeInfo nodeInfo;
    private Map<Integer, Socket> connections;
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
         this.nodeInfo = parser.nodes.get(nodeId);
         connections = new TreeMap<>();
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
            startServerThread();
        }
        else{
            startClientThread();
        }

    }

    private void startServerThread() {
    }

    //The client thread needs to connect to every other process.
    private void startClientThread() {
        parser.nodes.remove(nodeId);
        parser.nodes.forEach((k, v ) -> {
            int node = k;
            String host = v.getHostName();
            Integer port = v.getListenPort();
            int retries = 0;
            boolean connected = false;
            System.out.println("sleeping");
            while(!connected && retries < 3){
            try {
                retries++;
                Thread.sleep(2000);
                Socket socket = new Socket(host, port);
                connections.put(node,socket);
                connected = true;
                System.out.println("connected");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(!connected){
                System.out.println("som ting wong");
                throw new Exception("som ting wong");
            }
            System.out.println(k+""+v);
        });
    }

}
