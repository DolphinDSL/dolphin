package pt.lsts.nvl.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pt.lsts.nvl.runtime.tasks.Task;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;
import pt.lsts.nvl.util.Clock;
import pt.lsts.nvl.util.Debuggable;

public final class NVLRuntime implements Debuggable {

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

  private final NVLPlatform platform;
  private final NVLVehicleSet boundVehicles;
  
  private NVLRuntime(NVLPlatform p) {
    platform = p;
    boundVehicles = new NVLVehicleSet();
  }



  public NVLPlatform getPlatform() {
    return platform;
  }


  public void run(Task task) {
    NVLVehicleSet available = platform.getConnectedVehicles();

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

    NVLVehicleSet available = platform.getConnectedVehicles();
    available.removeAll(boundVehicles);
    
    d("Available vehicles: %d", available.size());

    for (NVLVehicle v : available) {
      d("  id=%s type=%s", v.getId(), v.getType());
    }

    NVLVehicleSet set = new NVLVehicleSet();
    for (VehicleRequirements req : reqList) {
      d("Matching requirement: %s", req.toString());
      Optional<NVLVehicle> ov = 
          available.stream()
          .filter(v -> !set.contains(v) && req.matchedBy(v))
          .findFirst();
      if (! ov.isPresent()) {
        d("Requirement was not met!");
        return NVLVehicleSet.EMPTY;
      }
      d("Requirement met by vehicle %s", ov.get().getId());
      set.add(ov.get());
    }
    boundVehicles.addAll(set);
    return set;
  }

  public NVLVehicleSet select(List<VehicleRequirements> reqList, double timeout) {
    double startTime = Clock.now();
    d("Performing selection with timeout %f", timeout);
    double delayTime = Math.max(1.0,  timeout * 0.1);
    NVLVehicleSet set = NVLVehicleSet.EMPTY;

    while (true) {
      set = select(reqList);
      if (set != NVLVehicleSet.EMPTY || Clock.now() - startTime >= timeout) {
        break;
      }
      pause(delayTime);
    } 
    return set;
  }
  
  public void release(NVLVehicleSet set) {
    boundVehicles.removeAll(set);
  }
  
  public void pause(double time) {
    try {
      Thread.sleep(Math.round(time * 1e+03));
    }
    catch (InterruptedException e) {
      throw new NVLExecutionException(e);
    }
  }
}

