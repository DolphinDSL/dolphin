package pt.lsts.nvl.runtime;

import java.util.List;

import pt.lsts.nvl.runtime.tasks.Task;

public interface NVLRuntime {

  List<NVLVehicle> getVehicles(List<VehicleRequirements> requirements);
  NVLVehicle getVehicle(String id);

  Task getTask(String id);


}
