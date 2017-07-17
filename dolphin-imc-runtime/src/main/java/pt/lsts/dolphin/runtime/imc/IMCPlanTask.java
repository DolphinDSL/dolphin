package pt.lsts.dolphin.runtime.imc;


import pt.lsts.dolphin.imc.AbstractIMCPlanTask;
import pt.lsts.imc.PlanSpecification;

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
