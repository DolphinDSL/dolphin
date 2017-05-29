package pt.lsts.nvl.runtime.imc;


import java.util.List;

import pt.lsts.nvl.imc.AbstractIMCPlanTask;
import pt.lsts.nvl.runtime.VehicleFilter;

public final class IMCPlanTask extends AbstractIMCPlanTask {

  public IMCPlanTask(String id) {
    super(id);
  }

  @Override
  public void getRequirements(List<VehicleFilter> requirements) {
    requirements.add(new VehicleFilter());
  }
  

  @Override
  public IMCPlanTaskExecutor getExecutor() {
    return new IMCPlanTaskExecutor(this);
  }

}
