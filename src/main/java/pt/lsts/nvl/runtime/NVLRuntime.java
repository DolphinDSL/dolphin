package pt.lsts.nvl.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.tasks.Task;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class NVLRuntime {

  private static  NVLRuntime INSTANCE;
  
  public static NVLRuntime getInstance() {
     if (INSTANCE == null) {
       INSTANCE = new NVLRuntime();
     }
     return INSTANCE;
  }
  
  private NVLRuntime() {
    this.platform = NVLPlatform.create();
  }

  private final NVLPlatform platform;

  public NVLPlatform getPlatform() {
    return platform;
  }


  public void run(Task task) {
    List<NVLVehicle> available = platform.getConnectedVehicles();
    
    Map<Task,List<NVLVehicle>> allocation = new HashMap<>();
    
    if (task.allocate(available, allocation) == false) {
      throw new NVLExecutionException("No vehicles to run task!");
    }
    TaskExecutor executor = task.getExecutor();
    executor.initialize(allocation);
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

