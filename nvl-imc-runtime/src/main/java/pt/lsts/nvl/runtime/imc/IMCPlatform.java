package pt.lsts.nvl.runtime.imc;

import pt.lsts.nvl.runtime.Platform;
import pt.lsts.nvl.runtime.NodeSet;
import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.util.Debuggable;

public final class IMCPlatform implements Platform, Debuggable {

  private final IMCCommunications comm = IMCCommunications.getInstance();
  
  public IMCPlatform() {
    if (!comm.isAlive()) {
      comm.start();
    }
  }
  
  @Override
  public PlatformTask getPlatformTask(String id) {
    return new IMCPlanTask(id);
  }

  @Override
  public NodeSet getConnectedVehicles() {
    return comm.getConnectedVehicles();
  }

  @Override
  public void displayMessage(String format, Object... args) {
     System.out.printf(format, args);
     System.out.println();
    
  }



}
