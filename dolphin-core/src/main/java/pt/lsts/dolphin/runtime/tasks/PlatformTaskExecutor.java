package pt.lsts.dolphin.runtime.tasks;

import pt.lsts.dolphin.runtime.Node;

import java.util.List;
import java.util.Map;

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

  protected final Node getVehicle() {return nodes.get(0);}

}
