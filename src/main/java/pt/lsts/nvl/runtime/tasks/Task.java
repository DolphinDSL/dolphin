package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;

public interface Task {
  String getId();
  TaskExecutor getExecutor();
  boolean allocate(List<NVLVehicle> available, Map<Task,List<NVLVehicle>> allocation);
}
