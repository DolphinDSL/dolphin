package pt.lsts.nvl.runtime.tasks;

import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public interface Task {
  String getId();
  void getRequirements(List<VehicleRequirements> requirements);
  TaskExecutor getExecutor();
}
