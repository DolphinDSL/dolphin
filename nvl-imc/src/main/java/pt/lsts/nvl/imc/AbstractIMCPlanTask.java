package pt.lsts.nvl.imc;


import java.util.List;

import pt.lsts.imc.PlanSpecification;
import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.runtime.tasks.PlatformTask;

public abstract class AbstractIMCPlanTask extends PlatformTask {

  private final PlanSpecification planSpec;
  
  protected AbstractIMCPlanTask(String id) {
    this(id, null);
  }
  
  protected AbstractIMCPlanTask(String id, PlanSpecification ps) {
    super(id);
    planSpec = ps;
  }
  
  protected final PlanSpecification getPlanSpecification() {
    return planSpec;
  }

  @Override
  public void getRequirements(List<VehicleRequirements> requirements) {
    requirements.add(new VehicleRequirements());
  }
  

  @Override
  public abstract AbstractIMCPlanExecutor getExecutor();
  

}
