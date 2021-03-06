import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Streams {

    private Map<Integer, ObjectInputStream> serverInputStreams;
    private Map<Integer, ObjectOutputStream> serverOutputStreams;
    private Map<Integer, Socket> clientSockets;
    private Map<Integer, Socket> serverSockets;
    private Queue<RequestMessage> requestQueue;
    private Queue<NodeInfo> criticalSectionQueue;
    private List<Long> times;
    private static Streams instance;
    private AtomicInteger messageCount;

    public AtomicInteger getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(AtomicInteger messageCount) {
        this.messageCount = messageCount;
    }

    public static Streams getInstance(){
        if(instance == null){
            instance = new Streams();
        }
        return instance;
    }


    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    private Streams(){
        messageCount = new AtomicInteger();
        this.criticalSectionQueue = new ConcurrentLinkedQueue<>();
        this.times = Collections.synchronizedList(new ArrayList<Long>());
        RequestMessageComparator comparator = new RequestMessageComparator();
        requestQueue = new PriorityBlockingQueue<>(16,comparator);
        clientSockets = new ConcurrentHashMap<>();
        serverSockets = new ConcurrentHashMap<>();
        serverInputStreams = new ConcurrentHashMap<>();
        serverOutputStreams = new ConcurrentHashMap<>();
        clientInputStreams = new ConcurrentHashMap<>();
        clientOutputStreams = new ConcurrentHashMap<>();
    }

    public Queue<NodeInfo> getCriticalSectionQueue() {
        return criticalSectionQueue;
    }

    public void setCriticalSectionQueue(Queue<NodeInfo> criticalSectionQueue) {
        this.criticalSectionQueue = criticalSectionQueue;
    }

    public Queue<RequestMessage> getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(Queue<RequestMessage> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public Map<Integer, Socket> getClientSockets() {
        return clientSockets;
    }

    public void setClientSockets(Map<Integer, Socket> clientSockets) {
        this.clientSockets = clientSockets;
    }

    public Map<Integer, Socket> getServerSockets() {
        return serverSockets;
    }

    public void setServerSockets(Map<Integer, Socket> serverSockets) {
        this.serverSockets = serverSockets;
    }

    private Map<Integer, ObjectInputStream> clientInputStreams;
    private Map<Integer, ObjectOutputStream> clientOutputStreams;


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
        System.out.println(map.keySet());
        System.out.println(map.get(1));
        System.out.println(map.get(2));
    }

    public void terminateAllStreams() {
        clientInputStreams.forEach((k,v)->{
            try {
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientOutputStreams.forEach((k,v)->{
            try {
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientSockets.forEach((k,v)->{
            try {
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverInputStreams.forEach((k,v)->{
            try {
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverOutputStreams.forEach((k,v)->{
            try {
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverSockets.forEach((k,v)->{
            try {
                v.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
