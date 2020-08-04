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
    private static boolean ready = false;

    public static boolean isReady() {
        return ready;
    }

    public static void setReady(boolean ready) {
        Process.ready = ready;
    }


    public static Process getInstance(int nodeId) throws IOException {
        if(instance == null){
            instance = new Process(nodeId);
        }
        return instance;
    }

    private Process(int nodeId) throws IOException {
        this.nodeId = nodeId;
         parser = Parser.getInstance("config.txt");
         nodeInfo = parser.nodes.get(nodeId);
         streams = Streams.getInstance();
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
        System.out.println("node"+nodeId+"Server thread hosting on:"+nodeInfo.getHostName()+"port:"+nodeInfo.getListenPort());
        try{
            System.out.println("nodesize"+parser.nodes.size());
            ServerSocket serverSocket = new ServerSocket(nodeInfo.getListenPort());
            for(int i = 0 ; i <parser.nodes.size(); i++){
                Server server = new Server(serverSocket,nodeId);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println(nodeId+ "Node has these servers sockets:");
        System.out.println(streams.getServerSockets().keySet());
    }

    //The client thread needs to connect to every other process.
    private void startClientThread() {
        parser.nodes.forEach((k, v ) -> {
            System.out.println("Target Node:"+k+"Client thread connecting to:"+v.hostName+"host, port:"+v.getListenPort()+ "This node:"+this.nodeId);
            if(k != nodeId){
                Client client = new Client(v,nodeId, k);
                while(!client.isConnected)
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }});
        //every client is connected.
        System.out.println(nodeId+"Every client is connected? Lets make sure.");
        System.out.println(nodeId+" Node has these client sockets:");
        System.out.println(streams.getClientSockets().keySet());
        System.out.println("Server keyset"+streams.getServerSockets().keySet());
        //this is where everything is ready.
        ready = true;

    }

//    private Socket getClientConnection(String host, int port) throws Exception{
//        int retries = 0;
//        boolean connected = false;
//        Socket socket = null;
//        while(!connected && retries < 3){
//            try {
//                retries++;
//                Thread.sleep(2000);
//                socket = new Socket(host, port);
//                connected = true;
//                System.out.println("connected");
//            }   catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//        if(socket == null){
//            return null;
//        }
//        return socket;
//    }

}
