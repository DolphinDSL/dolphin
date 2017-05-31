package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.Node;

public abstract class PlatformTaskExecutor extends TaskExecutor {

  private List<Node> vehicles = null;
  
  protected PlatformTaskExecutor(Task theTask) {
    super(theTask);
  }
  
  @Override
  protected void onInitialize(Map<Task,List<Node>> allocation) {
    vehicles = allocation.get(getTask());
  }
   
  protected final List<Node> getVehicles() {
    return vehicles;
  }

}
