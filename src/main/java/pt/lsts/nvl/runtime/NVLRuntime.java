package pt.lsts.nvl.runtime;

import java.util.List;

import pt.lsts.nvl.runtime.tasks.Task;

public interface NVLRuntime {

  List<NVLVehicle> getVehicles(Filter<NVLVehicle> filter);//substitute to VehicleRequirements?
  NVLVehicle getVehicle(String id);

  List<TaskSpecification> getTasks(Filter<TaskSpecification> filter);  
  Task getTask(String id);

  List<TaskExecution> launchTask(TaskSpecification task, List<NVLVehicle> vehicles);

}
