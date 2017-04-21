package pt.lsts.nvl.runtime;

import java.util.List;

public interface TaskSpecification {
  String getId();
  List<VehicleRequirements> getRequirements();

  // TODO remove
  void setRequirements(VehicleRequirements reqs);
}
