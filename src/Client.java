import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable{

    private NodeInfo nodeInfo;
    private int id;
    private int serverId;
    private Streams streams;

    public Client(NodeInfo nodeInfo, int id, int serverId, Streams streams){
        this.nodeInfo = nodeInfo;
        this.id = id;
        this.serverId = serverId;
        this.streams = streams;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        int retries = 0;
        boolean connected = false;
        while(retries < 3 && !connected) {
            try {
                Thread.sleep(1000);
                Socket socket = new Socket(this.nodeInfo.hostName, this.nodeInfo.getListenPort());
                connected = true;
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Message message = new Message();
                message.setNodeId(this.id);
                out.writeObject(message);
                System.out.println("Written object, adding streams");
                this.streams.getClientInputStreams().put(this.serverId, in);
                this.streams.getClientOutputStreams().put(this.serverId, out);
                this.streams.getClientSockets().put(this.id, socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                retries++;
            }
        }
    }

    public static void main(String args[]){
        NodeInfo info = new NodeInfo();
        info.setHostName("localhost");
        info.setListenPort(1234);
        Client client = new Client(info, 1, 3, new Streams());
    }
}