package pt.lsts.nvl.runtime;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import pt.lsts.nvl.runtime.tasks.Task;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class NVLRuntime {

  public NVLRuntime(NVLPlatform platform) {
    this.platform = platform;
  }

  private final NVLPlatform platform;

  public NVLPlatform getPlatform() {
    return platform;
  }

  public final boolean selectVehiclesForTask(Task task, List<NVLVehicle> available, List<NVLVehicle> selection) {
    selection.clear();
    List<VehicleRequirements> requirements = new LinkedList<>();
    task.getRequirements(requirements);
    for (VehicleRequirements r : requirements) {
      Optional<NVLVehicle> ov = available.stream().filter(v -> r.matchedBy(v)).findFirst();
      if (ov.isPresent()) {
        NVLVehicle v = ov.get();
        available.remove(v);
        selection.add(v);
      } else {
        break;
      }
    }
    boolean success = selection.size() == requirements.size();
    if (success) {
      selection.forEach(v -> v.setRunningTask(task)); 
    } else {
      // Undo
      available.addAll(selection);
    }
    return success;   
  }
  
  public void run(Task task) {
    List<NVLVehicle> available = platform.getConnectedVehicles();
    List<NVLVehicle> selected = new LinkedList<>();
    if (!selectVehiclesForTask(task, available, selected)) {
      throw new NVLExecutionException("Not enough vehicles");
    }
    TaskExecutor executor = task.getExecutor();
    executor.initialize(null);
    executor.start();
    while (executor.getState() != TaskExecutor.State.COMPLETED) {
      executor.step();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

}

