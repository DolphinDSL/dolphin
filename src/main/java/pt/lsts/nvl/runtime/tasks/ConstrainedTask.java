package pt.lsts.nvl.runtime.tasks;

import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public abstract class ConstrainedTask implements Task {

  protected final Task theTask;
  public ConstrainedTask(Task t) {
    theTask = t;
  }

  @Override
  public final String getId() {
    return theTask.getId();
  }

  @Override
  public final List<VehicleRequirements> getRequirements() {
    return theTask.getRequirements();
  }
}
