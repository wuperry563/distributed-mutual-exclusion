import java.io.IOException;

public class Application {
    public static void main(String[] args) throws InterruptedException, IOException {
        // TODO Auto-generated method stub

        if (args.length != 2) {
            System.out.println("Usage:\njava Project3 <nodeId> <config_file>");
            return;
        }

//        int nodeId = Integer.parseInt(args[0]);
//        String configLocation = args[1];
        Parser p = Parser.getInstance("config.txt");
        Process process = Process.getInstance(0);
    }
}
