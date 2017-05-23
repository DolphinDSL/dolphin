package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.NVLVehicleSet;

public class ResourceExplicitTask implements Task {

  private final Task theTask;
 
  private final NVLVehicleSet theVehicles;
  
  public ResourceExplicitTask(Task task, NVLVehicleSet vs) {
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
  public boolean allocate(NVLVehicleSet available,
                          Map<Task, List<NVLVehicle>> allocation) {
    return theTask.allocate(theVehicles, allocation);
  }

}
