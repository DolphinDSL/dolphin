package pt.lsts.nvl.runtime.imc;


import pt.lsts.imc.PlanSpecification;
import pt.lsts.nvl.imc.AbstractIMCPlanTask;

public final class IMCPlanTask extends AbstractIMCPlanTask {

  public IMCPlanTask(String id) {
    super(id);
  }
  
  public IMCPlanTask(PlanSpecification plan){
      super(plan.getPlanId(), plan);
  }

  @Override
  public IMCPlanTaskExecutor getExecutor() {
    return new IMCPlanTaskExecutor(this);
  }

}
