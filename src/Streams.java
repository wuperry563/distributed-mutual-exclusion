import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Streams {

    private Map<Integer, ObjectInputStream> serverInputStreams;
    private Map<Integer, ObjectOutputStream> serverOutputStreams;

    private Map<Integer, ObjectInputStream> clientInputStreams;
    private Map<Integer, ObjectOutputStream> clientOutputStreams;

    public Streams(){
        serverInputStreams = new ConcurrentHashMap<>();
        serverOutputStreams = new ConcurrentHashMap<>();
        clientInputStreams = new ConcurrentHashMap<>();
        clientOutputStreams = new ConcurrentHashMap<>();
    }

    public Map<Integer, ObjectInputStream> getServerInputStreams() {
        return serverInputStreams;
    }

    public void setServerInputStreams(Map<Integer, ObjectInputStream> serverInputStreams) {
        this.serverInputStreams = serverInputStreams;
    }

    public Map<Integer, ObjectInputStream> getClientInputStreams() {
        return clientInputStreams;
    }

    public void setClientInputStreams(Map<Integer, ObjectInputStream> clientInputStreams) {
        this.clientInputStreams = clientInputStreams;
    }

    public Map<Integer, ObjectOutputStream> getClientOutputStreams() {
        return clientOutputStreams;
    }

    public void setClientOutputStreams(Map<Integer, ObjectOutputStream> clientOutputStreams) {
        this.clientOutputStreams = clientOutputStreams;
    }

    public Map<Integer, ObjectOutputStream> getServerOutputStreams() {
        return serverOutputStreams;
    }

    public void setServerOutputStreams(Map<Integer, ObjectOutputStream> serverOutputStreams) {
        this.serverOutputStreams = serverOutputStreams;
    }

    public static void main(String args[]){
        Map<Integer, String> map = new ConcurrentHashMap<>();
        map.put(1,"lmao");
        System.out.println(map.get(1));
        System.out.println(map.get(2));
    }
}
