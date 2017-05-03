package pt.lsts.nvl.runtime;

import java.util.List;

import pt.lsts.nvl.runtime.tasks.PlatformTask;

public interface NVLPlatform {
  
  String NVL_PLATFORM_PROPERTY = "nvl.platform.class";
  
  static NVLPlatform create() {
    try {
      Class<?> theClass = Class.forName(System.getProperty(NVL_PLATFORM_PROPERTY));
      return (NVLPlatform) theClass.newInstance();
    }
    catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new NVLExecutionException(e); 
    }
  }
  
  List<NVLVehicle> getConnectedVehicles();
  
  PlatformTask getPlatformTask(String id);
}
