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
    public static int numNodes;
    public static int requestDelay;
    public static int meanCS;
    public static int numRequests;
    public static Map<Integer,NodeInfo> nodes;

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
        for(int i = 0; i<instance.numNodes; i++){
            String[]configNode = configArray.get(i+1).split(" ");
            node = new NodeInfo();
            int id = Integer.parseInt(configNode[0]);
            node.setHostName(configNode[1]);
            node.setListenPort(Integer.parseInt(configNode[2]));
            instance.nodes.put(id, node);
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
        int []vars = new int[4];
       String[] globalVars = instance.configArray.get(0).split(" ");
       for(int i = 0; i<globalVars.length;i++){
           vars[i] = Integer.parseInt(globalVars[i]);
       }
       instance.numNodes = vars[0];
       instance.requestDelay = vars[1];
       instance.meanCS = vars[2];
       instance.numRequests = vars[3];
    }

    public static void main(String args[]){
        try{
            Parser p = Parser.getInstance("config.txt");
        }catch(Exception e){

        }

    }
}
