package pt.lsts.nvl.standalone;

import java.util.Arrays;
import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.runtime.tasks.Task;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class IMCTask implements Task {

  private final String id;
  
  public IMCTask(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<VehicleRequirements> getRequirements() {
    return Arrays.asList(new VehicleRequirements());
  }

  @Override
  public TaskExecutor getExecutor() {
    return new IMCTaskExecutor(this);
  }

}
