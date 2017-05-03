package pt.lsts.nvl.runtime.imc;

import java.util.List;

import pt.lsts.nvl.runtime.NVLPlatform;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.tasks.PlatformTask;

public class IMCPlatform implements NVLPlatform {

  private final IMCCommunications comm = IMCCommunications.getInstance();
  
  public IMCPlatform() {
    if (!comm.isAlive()) {
      comm.start();
    }
  }
  
  @Override
  public PlatformTask getPlatformTask(String id) {
    return new IMCTask(id);
  }

  @Override
  public List<NVLVehicle> getConnectedVehicles() {
    return comm.getConnectedVehicles();
  }



}
