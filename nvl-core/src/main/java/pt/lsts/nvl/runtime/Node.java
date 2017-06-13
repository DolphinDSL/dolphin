package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.Task;

public interface Node {
   String getId();
   String getType();
   Position getPosition();
   Payload getPayload();
   Task getRunningTask();
   void setRunningTask(Task task);
   void release();
}
