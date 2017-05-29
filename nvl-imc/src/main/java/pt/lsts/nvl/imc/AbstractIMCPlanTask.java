package pt.lsts.nvl.imc;


import java.util.List;

import pt.lsts.imc.PlanSpecification;
import pt.lsts.nvl.runtime.VehicleFilter;
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
  public void getRequirements(List<VehicleFilter> requirements) {
    requirements.add(new VehicleFilter());
  }
  

  @Override
  public abstract AbstractIMCPlanExecutor getExecutor();
  

}
