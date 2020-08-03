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

    private Process(int nodeId) throws IOException {
        this.nodeId = nodeId;
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
            for(int i = 0 ; i <parser.nodes.size()-1; i++){
                Server server = new Server(serverSocket,streams);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //The client thread needs to connect to every other process.
    private void startClientThread() {
        parser.nodes.forEach((k, v ) -> {
            if(k != nodeId){
                while(streams.getClientSockets().get(k) == null ){
                    try{
                        Thread.sleep(1000);
                        Client client = new Client(v,nodeId, k ,streams);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }});
        //every client is connected.
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
