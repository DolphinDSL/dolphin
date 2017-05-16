package pt.lsts.nvl.runtime;

import java.util.List;

import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.util.Debuggable;

public interface NVLPlatform extends Debuggable {
  
  List<NVLVehicle> getConnectedVehicles();
  
  PlatformTask getPlatformTask(String id);
  
  void nvlInfoMessage(String format, Object... args);
}
