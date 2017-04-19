package pt.lsts.nvl.runtime.tasks;

import java.util.Collections;
import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public abstract class AbstractTask implements Task {

  private final String id;
  private final List<VehicleRequirements> reqs;
  
  protected AbstractTask(String id, List<VehicleRequirements> reqs) {
    this.id = id;
    this.reqs = Collections.unmodifiableList(reqs);
  }
  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<VehicleRequirements> getRequirements() {
    return reqs;
  }


}
