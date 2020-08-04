import java.io.IOException;

public class Application{
    private Streams streams;
    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        if (args.length != 2) {
            System.out.println("Usage:\njava Project2 <nodeId> <config_file>");
            return;
        }
        int nodeId = Integer.parseInt(args[0]);
        String configLocation = args[1];
        Parser p = Parser.getInstance(configLocation);
        System.out.println("node running with node id "+nodeId);
        Process process = Process.getInstance(nodeId);
        while (!process.isReady()){
            Thread.sleep(5000);
        }
        Streams streams = process.getStreams();
        System.out.println(nodeId+"Ready to start algo. Client Sockets:\n"
        +streams.getClientSockets().keySet());

        Modules modules = new Modules(streams,nodeId);
    }

}
