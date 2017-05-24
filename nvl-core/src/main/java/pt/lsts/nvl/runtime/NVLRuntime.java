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

  private NVLRuntime(NVLPlatform platform) {
    this.platform = platform;
  }

  private final NVLPlatform platform;

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

  public boolean select(double timeout, Map<String,VehicleRequirements> reqs, Map<String,NVLVehicleSet> choice) {
    double startTime = Clock.now();
    d("Performing selection with timeout %f", timeout);
    long delayTime = Math.max(1000, (Math.round(timeout) * 1000) / 10);

    while (Clock.now() - startTime < timeout) {
      if (select(reqs,choice)) {
        return true;
      }
      try {
        Thread.sleep(delayTime);
      }
      catch(InterruptedException e) {
        throw new NVLExecutionException(e);
      }
    }
    return false;
  }
  public boolean select(Map<String,VehicleRequirements> reqs, Map<String,NVLVehicleSet> choice) {

    NVLVehicleSet available = platform.getConnectedVehicles();

    d("Available vehicles: %d", available.size());

    for (NVLVehicle v : available) {
      d("  id=%s type=%s", v.getId(), v.getType());
    }

    NVLVehicleSet chosen = new NVLVehicleSet();
    choice.clear();

    for (Map.Entry<String,VehicleRequirements> e : reqs.entrySet()) {
      String id = e.getKey();
      VehicleRequirements vr = e.getValue();
      d("Matching requirement: %s -> %s", id, vr.toString());
      Optional<NVLVehicle> ov = 
          available.stream()
          .filter(v -> !chosen.contains(v) && vr.matchedBy(v))
          .findFirst();
      if (! ov.isPresent()) {
        d("Requirement was not met!");
        return false;
      }
      d("Requirement met by vehicle %s", ov.get().getId());
      NVLVehicle v = ov.get();
      chosen.add(v);
      choice.put(id, NVLVehicleSet.singleton(ov.get()));
    }
    return true;
  }

  public NVLVehicleSet select(List<VehicleRequirements> reqList) {

    NVLVehicleSet available = platform.getConnectedVehicles();

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
    return set;
  }

  public NVLVehicleSet select(List<VehicleRequirements> reqList, double timeout) {
    double startTime = Clock.now();
    d("Performing selection with timeout %f", timeout);
    double delayTime = Math.max(1.0,  timeout * 0.1);
    NVLVehicleSet set = NVLVehicleSet.EMPTY;

    while (Clock.now() - startTime < timeout) {
      set = select(reqList);
      if (set != NVLVehicleSet.EMPTY) {
        break;
      }
      pause(delayTime);
    }
    return set;
  }
  
  public static void pause(double time) {
    try {
      Thread.sleep(Math.round(time * 1e+03));
    }
    catch (InterruptedException e) {
      throw new NVLExecutionException(e);
    }
  }
}

