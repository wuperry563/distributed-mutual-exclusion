

public class PollResponseMessage extends Message{

    private boolean executingCritical;

    public boolean isExecutingCritical() {
        return executingCritical;
    }

    public void setExecutingCritical(boolean executingCritical) {
        this.executingCritical = executingCritical;
    }

    public PollResponseMessage(int nodeId, boolean executingCritical){
        super("",nodeId);
        this.executingCritical = executingCritical;
    }

}
