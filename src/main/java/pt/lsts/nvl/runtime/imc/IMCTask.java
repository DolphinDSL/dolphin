package pt.lsts.nvl.runtime.imc;

import static pt.lsts.nvl.util.Debug.d;

import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class IMCTask extends PlatformTask {

  public IMCTask(String id) {
    super(id);
  }

  @Override
  public void getRequirements(List<VehicleRequirements> requirements) {
    requirements.add(new VehicleRequirements());
    d("Requirements: %d / %s", requirements.size(), requirements);
  }
  

  @Override
  public TaskExecutor getExecutor() {
    return new IMCTaskExecutor(this);
  }

}
