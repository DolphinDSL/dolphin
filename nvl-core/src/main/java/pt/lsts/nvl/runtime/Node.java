package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.Task;

public interface Node {
  
   double MIN_CONNECTION_TIMEOUT = 5.0;
   double MAX_CONNECTION_TIMEOUT = 300.0;
   double INITIAL_CONNECTION_TIMEOUT_SETTING = 20.0;
   
   String getId();
   String getType();
   Position getPosition();
   Payload getPayload();
   void release();
   Task getRunningTask();
   void setRunningTask(Task task);
   double getConnectionTimeout();
   void setConnectionTimeout(double timeout);
   


}
