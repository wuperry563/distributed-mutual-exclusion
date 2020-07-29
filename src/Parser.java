import javax.swing.tree.TreeNode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

    public static final String LINE_SEPARATOR_PROPERTY = System.getProperty("line.separator");
    public static Parser instance = null;
    private static String config = "";
    private static List<String> configArray;
    public static int numNodes, minPerActive, maxPerActive, minSendDelay, snapshotDelay, maxNumber;
    public static Map<Integer,NodeInfo> nodes;
    public static TreeNode curTreeNode = null;
    public static int MESSAGE_SIZE = 50;

//    The first valid line of the configuration file contains four tokens. The first token is the number
//    of nodes in the system. The second token is the mean value for inter-request delay (in milliseconds).
//    The third token is the mean value for cs-execution time (in milliseconds). The fourth token is the
//    number of requests each node should generate.
    private Parser() {
    }

    public static Parser getInstance(String configFile) throws FileNotFoundException, IOException {
        if(instance == null){
            instance = new Parser();
            String rawConfig = new String(Files.readAllBytes(Paths.get(configFile)));
            instance.config = rawConfig.replaceAll("(?m)^#.*", "");
            instance.configArray = Arrays.asList(instance.config.split(LINE_SEPARATOR_PROPERTY));
            nodes = new TreeMap<Integer,NodeInfo>();
            removeLineSeparation();
            setGlobalVariables();
            createNodeInfo();
        }
        return instance;
    }

    private static void createNodeInfo() {
        NodeInfo node;
//        List<Integer> neighbors;
        for(int i = 0; i<instance.numNodes; i++){
//            System.out.println(configArray.get(i+1));
            String[]configNode = configArray.get(i+1).split(" ");
            node = new NodeInfo();
            int id = Integer.parseInt(configNode[0]);
            node.setHostName(configNode[1]);
            node.setListenPort(Integer.parseInt(configNode[2]));
            instance.nodes.put(id, node);
        }
        for(int i = 1+instance.numNodes; i < configArray.size(); i++){
            List<Integer> neighbors = new ArrayList<>();
            int nodeNum = i-1-instance.numNodes;
            String[] lmao = configArray.get(i).split(" ");
            Arrays.stream(configArray.get(i).split(" ")).forEach(
                    s -> {neighbors.add(Integer.parseInt(s));}
            );
            nodes.get(nodeNum).setNeighbors(neighbors);
        }
    }

    private static void removeLineSeparation() {
        List<String> newConfigArray = new ArrayList<>();
        for(int i = 0 ; i < instance.configArray.size();i++){
            String s = instance.configArray.get(i);
            if(!s.equals("")){
                newConfigArray.add(s);
            }
        }
        instance.configArray = newConfigArray;
    }

    public static void setGlobalVariables(){
        int []vars = new int[6];
       String[] globalVars = instance.configArray.get(0).split(" ");
       for(int i = 0; i<globalVars.length;i++){
           vars[i] = Integer.parseInt(globalVars[i]);
//           System.out.println("global variable added: "+vars[i]);
       }
       instance.numNodes = vars[0];
       instance.minPerActive = vars[1];
       instance.maxPerActive = vars[2];
       instance.minSendDelay = vars[3];
       instance.snapshotDelay = vars[4];
       instance.maxNumber = vars[5];
    }

    public static void main(String args[]){

    }
}
