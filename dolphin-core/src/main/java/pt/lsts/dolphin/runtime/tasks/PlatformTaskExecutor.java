package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public abstract class PlatformTaskExecutor extends TaskExecutor {

  private List<Node> nodes = null;
  
  protected PlatformTaskExecutor(Task theTask) {
    super(theTask);
  }
  
  @Override
  protected void onInitialize(Map<Task,List<Node>> allocation) {
    d("allocation: %s %s", getTask(), allocation.get(getTask()));
    nodes = allocation.get(getTask());
  }
   
  protected final List<Node> getVehicles() {
    return nodes;
  }

}
