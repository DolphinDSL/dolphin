package pt.lsts.dolphin.runtime;


import pt.lsts.dolphin.runtime.tasks.Task;

public interface Node {
  
   double MIN_CONNECTION_TIMEOUT = 5.0;
   double MAX_CONNECTION_TIMEOUT = 3600.0;
   double INITIAL_CONNECTION_TIMEOUT_SETTING = 20.0;
   
   static void assertValidTimeout(double timeout) {
     if (timeout < MIN_CONNECTION_TIMEOUT || timeout > MAX_CONNECTION_TIMEOUT) {
       throw new EnvironmentException("Invalid timeout value: " + timeout);
     }
   }

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
