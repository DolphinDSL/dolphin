package pt.lsts.nvl.runtime.tasks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.VehicleRequirements;

public abstract class PlatformTask implements Task { 
  
 private final String id;
  
  public PlatformTask(String id) {
    this.id = id;
  }

  @Override
  public final String getId() {
    return id;
  }
  
  public abstract void getRequirements(List<VehicleRequirements> requirements);
  
  public final boolean allocate(List<NVLVehicle> available, Map<Task,List<NVLVehicle>> allocation) {
    LinkedList<VehicleRequirements> requirements = new LinkedList<>();
    LinkedList<NVLVehicle> selection = new LinkedList<>();
    getRequirements(requirements);
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
      allocation.put(this, selection); 
    } else {
      // Undo
      available.addAll(selection);
    }
    return success;
  }
  
  
  
}


