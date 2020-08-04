import java.sql.Timestamp;
import java.util.Comparator;

public class RequestMessageComparator implements Comparator<RequestMessage> {

    @Override
    /*
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     */
    public int compare(RequestMessage o1, RequestMessage o2) {
        Timestamp t1 = o1.getTimestamp();
        Timestamp t2 = o2.getTimestamp();
        if(t1.compareTo(t2) == 0 ){
            return o1.getNodeId() - o2.getNodeId();
        }
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}
