package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.Task;

public abstract class AbstractNode implements Node {
   
   private static double gDefaultConnectionTimeout = INITIAL_CONNECTION_TIMEOUT_SETTING;
   
   public static void setDefaultConnectionTimeout(double timeout) {
     assertValidTimeout(timeout);
     gDefaultConnectionTimeout = timeout;
   }
   
   private static void assertValidTimeout(double timeout) {
     if (timeout < MIN_CONNECTION_TIMEOUT || timeout > MAX_CONNECTION_TIMEOUT) {
       throw new EnvironmentException("Invalid timeout value: " + timeout);
     }
   }
   
   public static double getDefaultConnectionTimeout() {
     return gDefaultConnectionTimeout;
   }
   
   private final String nodeId;
   private Task runningTask;
   private double connectionTimeout;
   
   protected AbstractNode(String id) {
     System.out.println(id);
     nodeId = id;
     connectionTimeout = getDefaultConnectionTimeout();
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
     assertValidTimeout(timeout);
     connectionTimeout = timeout;
   }
   
   public abstract String getType();
   public abstract Position getPosition();
   public abstract Payload getPayload();
   public abstract void release();

}
