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
    LinkedList<NVLVehicle> selection = new LinkedList<>();
    LinkedList<VehicleRequirements> requirements = new LinkedList<>();
    getRequirements(requirements);
    
    d("Requirements: %s", requirements);
    d("Vehicles: %s", available);
  
    for (VehicleRequirements r : requirements) {
      Optional<NVLVehicle> optV = available.stream().filter(v -> r.matchedBy(v)).findFirst();
      if (optV.isPresent()) {
        NVLVehicle v = optV.get();
        available.remove(v);
        selection.add(v);
        d("Selected: %s", v);
      } else {
        d("No match for %s", r);
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


