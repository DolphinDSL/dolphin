package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;

public abstract class PlatformTaskExecutor extends TaskExecutor {

  private List<NVLVehicle> vehicles = null;
  
  protected PlatformTaskExecutor(Task theTask) {
    super(theTask);
  }
  
  @Override
  protected void onInitialize(Map<Task,List<NVLVehicle>> allocation) {
    vehicles = allocation.get(getTask());
  }
   
  protected final List<NVLVehicle> getVehicles() {
    return vehicles;
  }

}
