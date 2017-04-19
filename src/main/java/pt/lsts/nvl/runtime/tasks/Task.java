package pt.lsts.nvl.runtime.tasks;

import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public interface Task {
  String getId();
  List<VehicleRequirements> getRequirements();
}
