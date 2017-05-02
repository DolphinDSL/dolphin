package pt.lsts.nvl.standalone;

import java.util.List;

import pt.lsts.nvl.runtime.Filter;
import pt.lsts.nvl.runtime.NVLRuntime;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.TaskExecution;
import pt.lsts.nvl.runtime.TaskSpecification;
import pt.lsts.nvl.runtime.tasks.Task;

public class StandaloneRuntime implements NVLRuntime {

  
  @Override
  public List<NVLVehicle> getVehicles(Filter<NVLVehicle> filter) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NVLVehicle getVehicle(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<TaskSpecification> getTasks(Filter<TaskSpecification> filter) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Task getTask(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<TaskExecution> launchTask(TaskSpecification task,
      List<NVLVehicle> vehicles) {
    // TODO Auto-generated method stub
    return null;
  }

}
