import java.util.List;

public class NodeInfo {
    List<Integer> neighbors;
    Integer listenPort;
    String hostName;

    public List<Integer> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Integer> neighbors) {
        this.neighbors = neighbors;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getListenPort() {
        return listenPort;
    }

    public void setListenPort(Integer listenPort) {
        this.listenPort = listenPort;
    }
}
