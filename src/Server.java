import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class based on incoming connection request(launches a thread for each
 * incoming reuqest), adds the connection information to corresponding service
 * provider's HashMap object.
 */
public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Streams streams;

    public Server(ServerSocket serverSocket, Streams streams) {
        this.serverSocket = serverSocket;
        this.streams = streams;
        Thread t = new Thread(this);
        t.start();
    }


    public void run(){
        System.out.println("attempting ");
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            Socket socket = this.serverSocket.accept();
            System.out.println("server accepted");
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("in");
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("out");
            Message m = (Message) in.readObject();
            int clientNode = m.getNodeId();
            streams.getServerInputStreams().put(clientNode,in);
            streams.getServerOutputStreams().put(clientNode,out);
            System.out.println("streams set for node "+clientNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception{
        Parser p = Parser.getInstance("config.txt");
        NodeInfo nodeInfo = p.nodes.get(0);
        ServerSocket serverSocket = new ServerSocket(nodeInfo.getListenPort());
            for(int i = 0 ; i< 2; i++){
                System.out.println("HostPort+ " +nodeInfo.getListenPort());
                Server server = new Server(serverSocket, new Streams());
            }
//        });
        for(int i = 0 ; i<3; i++){
            System.out.println("aslkdfj");
            int retries = 0;
            boolean connected = false;
            while(retries < 3 && !connected) {
                try {
                    Thread.sleep(1000);
                    Socket socket = new Socket("localhost", nodeInfo.getListenPort());
                    connected = true;
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Message message = new Message();
                    message.setNodeId(1);
                    message.setMessage("lmao");
                    out.writeObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally{
                    retries++;
                }
            }
            System.out.println("break");
        }
    }
}
