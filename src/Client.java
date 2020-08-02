import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable{

    private NodeInfo nodeInfo;

    public Client(NodeInfo nodeInfo, int id){
        this.nodeInfo = nodeInfo;
        Thread t = new Thread(this);
        System.out.println("attempting to start at"+ nodeInfo.getHostName()+ " port "+ nodeInfo.getListenPort());
        try{
            Parser.getInstance("config.txt").nodes.remove(id);
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        t.start();
    }

    @Override
    public void run() {
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
                message.setMessage("lmao");
                out.writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                retries++;
            }
        }
    }
}
