package pt.lsts.nvl.runtime.imc;

import java.util.Arrays;
import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class IMCTask implements PlatformTask {

  private final String id;
  
  public IMCTask(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void getRequirements(List<VehicleRequirements> requirements) {
    requirements.add(new VehicleRequirements());
  }

  @Override
  public TaskExecutor getExecutor() {
    return new IMCTaskExecutor(this);
  }

}
