package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.NodeSet;

public class ResourceExplicitTask implements Task {

  private final Task theTask;
 
  private final NodeSet theVehicles;
  
  public ResourceExplicitTask(Task task, NodeSet vs) {
     theTask = task;
     theVehicles = vs;
  }
      
  @Override
  public String getId() {
    return theTask.getId();
  }

  @Override
  public TaskExecutor getExecutor() {
    return theTask.getExecutor();
  }

  @Override
  public boolean allocate(NodeSet available,
                          Map<Task, List<Node>> allocation) {
    return theTask.allocate(theVehicles, allocation);
  }

}
