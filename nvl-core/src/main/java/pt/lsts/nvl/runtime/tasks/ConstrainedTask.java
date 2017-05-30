package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.NVLVehicleSet;

public abstract class ConstrainedTask implements Task {

  protected final Task theTask;

  public ConstrainedTask(Task t) {
    theTask = t;
  }

  @Override
  public final String getId() {
    return theTask.getId();
  }

  @Override
  public final boolean allocate(NVLVehicleSet available, Map<Task, List<NVLVehicle>> allocation) {
    return theTask.allocate(available, allocation);
  }
  
  @Override
  public abstract ConstrainedTaskExecutor getExecutor();
}
