package pt.lsts.nvl.runtime;

import java.util.List;

import pt.lsts.nvl.runtime.tasks.Task;

public interface Node {
   String getId();
   String getType();
   Position getPosition();
   List<PayloadComponent> getPayload();
   Task getRunningTask();
   void setRunningTask(Task task);
}
