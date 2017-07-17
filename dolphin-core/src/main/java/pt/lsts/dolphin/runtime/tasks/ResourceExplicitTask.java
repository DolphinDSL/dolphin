package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

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
    NodeSet consider = NodeSet.intersection(available, theVehicles);
    NodeSet ignore = NodeSet.difference(available, theVehicles);
    boolean success = theTask.allocate(consider, allocation);
    if (success) {
      available.clear();
      available.addAll(ignore); // not used by this task
      available.addAll(consider); // whatever's left from allocating to this task
    }
    return success;
  }

}
