package pt.lsts.nvl.imc;


import java.util.List;

import pt.lsts.imc.PlanSpecification;
import pt.lsts.nvl.runtime.NodeFilter;
import pt.lsts.nvl.runtime.tasks.PlatformTask;

public abstract class AbstractIMCPlanTask extends PlatformTask {

  private final PlanSpecification planSpec;
  private final Payload payload;
  protected AbstractIMCPlanTask(String id) {
    this(id, null);
  }
  
  protected AbstractIMCPlanTask(String id, PlanSpecification ps) {
    super(id);
    planSpec = ps;
    List<PayloadComponent> components = new ArrayList<>();
    if(ps!=null){
    	//TODO
    }
    payload = new Payload(components);
  }
  
  protected final PlanSpecification getPlanSpecification() {
    return planSpec;
  }

  @Override
  public void getRequirements(List<NodeFilter> requirements) {
	NodeFilter filter = new NodeFilter();
	filter.setRequiredPayload(payload);
    requirements.add(filter);
  }
  

  @Override
  public abstract AbstractIMCPlanExecutor getExecutor();
  

}
