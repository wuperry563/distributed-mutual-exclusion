import java.io.IOException;
import java.net.ServerSocket;
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
    private static Streams streams;

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
         nodeInfo = parser.nodes.get(nodeId);
         streams = new Streams();
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
        try{
            ServerSocket serverSocket = new ServerSocket(nodeInfo.getListenPort());
            for()
            Server server = new Server(serverSocket,streams);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

    }

    //The client thread needs to connect to every other process.
    private void startClientThread() {
        Client client = new Client(nodeInfo, nodeId);
        parser.nodes.remove(nodeId);
        parser.nodes.forEach((k, v ) -> {
            if(k != nodeId){
                int node = k;
                String host = v.getHostName();
                Integer port = v.getListenPort();
                try{
                    Socket socket = getClientConnection(host,port);
                    if(socket == null){
                        System.out.println("unable to obtain socket connection");
                        System.exit(0);
                    }
                    connections.put(node,socket);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                System.out.println("sleeping");

                System.out.println(k+""+v);
            }});
    }

    private Socket getClientConnection(String host, int port) throws Exception{
        int retries = 0;
        boolean connected = false;
        Socket socket = null;
        while(!connected && retries < 3){
            try {
                retries++;
                Thread.sleep(2000);
                socket = new Socket(host, port);
                connected = true;
                System.out.println("connected");
            }   catch(Exception e){
                e.printStackTrace();
            }
        }
        if(socket == null){
            return null;
        }
        return socket;
    }

}
