package pt.lsts.nvl.runtime.imc;


import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class IMCPlanTask extends PlatformTask {

  public IMCPlanTask(String id) {
    super(id);
  }

  @Override
  public void getRequirements(List<VehicleRequirements> requirements) {
    requirements.add(new VehicleRequirements());
  }
  

  @Override
  public TaskExecutor getExecutor() {
    return new IMCPlanTaskExecutor(this);
  }

}
