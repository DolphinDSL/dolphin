package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.util.Debuggable;

public interface Task extends Debuggable {
  String getId();
  TaskExecutor getExecutor();
  boolean allocate(List<NVLVehicle> available, Map<Task,List<NVLVehicle>> allocation);

}
