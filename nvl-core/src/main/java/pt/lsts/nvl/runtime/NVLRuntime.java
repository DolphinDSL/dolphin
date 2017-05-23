package pt.lsts.nvl.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pt.lsts.nvl.runtime.tasks.Task;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;
import pt.lsts.nvl.util.Clock;

public final class NVLRuntime {

  private static NVLRuntime INSTANCE;

  public static NVLRuntime create(NVLPlatform platform) {
    if (INSTANCE != null) {
      throw new NVLExecutionException("Runtime has already been created");
    } 
    INSTANCE = new NVLRuntime(platform);
    return INSTANCE;
  }

  public static NVLRuntime getInstance() {
    if (INSTANCE == null) {
      throw new NVLExecutionException("Runtime has not been created");
    }
    return INSTANCE;
  }

  private NVLRuntime(NVLPlatform platform) {
    this.platform = platform;
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

  public NVLVehicleSet select(List<VehicleRequirements> reqList) {
    
    List<NVLVehicle> available = platform.getConnectedVehicles();
    NVLVehicleSet set = new NVLVehicleSet();
    for (VehicleRequirements req : reqList) {
      Optional<NVLVehicle> ov = 
        available.stream()
                 .filter(v -> !set.contains(v) && req.matchedBy(v))
                 .findFirst();
      if (!ov.isPresent()) {
        return NVLVehicleSet.EMPTY;
      }
      set.add(ov.get());
    }
    return set;
  }

 public NVLVehicleSet select(List<VehicleRequirements> reqList, double timeout) {
    double startTime = Clock.now();
    
    NVLVehicleSet set = NVLVehicleSet.EMPTY;
    
    while (Clock.now() - startTime < timeout) {
      set = select(reqList);
      if (set != NVLVehicleSet.EMPTY) {
        break;
      }
      try {
        Thread.sleep(100);
      }
      catch(InterruptedException e) {
        throw new NVLExecutionException(e);
      }
    }
    return set;
   
  }
}

