package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.util.Debuggable;

public interface NVLPlatform extends Debuggable {
  
  NVLVehicleSet getConnectedVehicles();
  
  PlatformTask getPlatformTask(String id);
  
  void nvlInfoMessage(String format, Object... args);
}
