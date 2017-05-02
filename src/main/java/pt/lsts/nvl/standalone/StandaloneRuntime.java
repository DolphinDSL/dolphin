package pt.lsts.nvl.standalone;

import java.util.List;

import pt.lsts.nvl.runtime.NVLRuntime;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.runtime.tasks.Task;

public class StandaloneRuntime implements NVLRuntime {



  @Override
  public NVLVehicle getVehicle(String id) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Task getTask(String id) {
    return new IMCTask(id);
  }



  @Override
  public List<NVLVehicle> getVehicles(List<VehicleRequirements> requirements) {
    // TODO Auto-generated method stub
    return null;
  }

}
