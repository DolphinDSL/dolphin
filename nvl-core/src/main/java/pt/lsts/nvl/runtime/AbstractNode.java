package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.Task;

public abstract class AbstractNode implements Node {
   

   
   private final String nodeId;
   private Task runningTask;
   private double connectionTimeout;
   
   protected AbstractNode(String id) {
     System.out.println(id);
     nodeId = id;
   }
   
   public final String getId() {
     return nodeId;
   }
   public final Task getRunningTask() {
     return runningTask;
   }
   
   public final void setRunningTask(Task task) {
     runningTask = task;
   }
   public final double getConnectionTimeout() {
     return connectionTimeout;
   }
   public final void setConnectionTimeout(double timeout) {
     Node.assertValidTimeout(timeout);
     connectionTimeout = timeout;
   }
   
   public abstract String getType();
   public abstract Position getPosition();
   public abstract Payload getPayload();
   public abstract void release();

}
