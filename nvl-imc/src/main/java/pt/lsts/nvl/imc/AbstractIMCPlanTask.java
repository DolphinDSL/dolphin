package pt.lsts.nvl.imc;


import java.util.ArrayList;
import java.util.List;

import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.SetEntityParameters;
import pt.lsts.nvl.runtime.NodeFilter;
import pt.lsts.nvl.runtime.Payload;
import pt.lsts.nvl.runtime.PayloadComponent;
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

    if(ps!=null)
      payload = new Payload(getPayloadComponents(ps));
    else
      payload = null;

  }

  private List<PayloadComponent> getPayloadComponents(PlanSpecification ps){
    List<PayloadComponent> components = new ArrayList<>();

    d("> plan %s", ps.getPlanId());
    for(PlanManeuver maneuver: ps.getManeuvers()) {
      for(IMCMessage action: maneuver.getStartActions()) {
        if (! (action instanceof SetEntityParameters))  {
          continue;
        }
        SetEntityParameters sep = ((SetEntityParameters) action);
        PayloadComponent payload = new PayloadComponent(sep.getName());
        d("  > maneuver %s: Payload %s ", maneuver.getManeuverId(), payload.getName());
        if (components.contains(payload)) {
          continue;
        }
        boolean isActive = false;
        for (EntityParameter param: sep.getParams()) {
          d("    > parameter: %s = %s", param.getName(), param.getValue());
          if (param.getName().equalsIgnoreCase("Active") && param.getValue().equalsIgnoreCase("true")){
            isActive = true;
          }
        }
        if (isActive) {
          d("%s is required!", payload.getName());
          components.add(new PayloadComponent(payload.getName()));
        }
      }
    }
    d("Payload components: %s", components);
    return components;
  }

  protected final PlanSpecification getPlanSpecification() {
    return planSpec;
  }

  @Override
  public List<NodeFilter> getRequirements() {
    List<NodeFilter> requirements = new ArrayList<>();
    NodeFilter filter = new NodeFilter();
    filter.setRequiredPayload(payload);
    requirements.add(filter);
    return requirements;
  }

  @Override
  public String toString(){
    return getId();
  }


  @Override
  public abstract AbstractIMCPlanExecutor getExecutor();


}
