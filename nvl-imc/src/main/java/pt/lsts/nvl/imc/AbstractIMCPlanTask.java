package pt.lsts.nvl.imc;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.SetEntityParameters;
import pt.lsts.imc.EntityParameter;
import pt.lsts.nvl.runtime.NodeFilter;
import pt.lsts.nvl.runtime.Payload;
import pt.lsts.nvl.runtime.PayloadComponent;
import pt.lsts.nvl.runtime.tasks.PlatformTask;

public abstract class AbstractIMCPlanTask extends PlatformTask {

  private final PlanSpecification planSpec;
  private final Payload payload;
  private boolean isActive = false;
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
    for(PlanManeuver maneuver: ps.getManeuvers()){
      for(IMCMessage action: maneuver.getStartActions()) {
        if ( action instanceof SetEntityParameters) {
          SetEntityParameters entityP = ((SetEntityParameters) action);
          PayloadComponent payload = new PayloadComponent(entityP.getName());
          d("  > maneuver %s: Payload %s ", maneuver.getManeuverId(), payload.getName());
      
//          if (! components.contains(payload)) {
//            components.add(new PayloadComponent(payload.getName()));
//          }
          // TODO: are parameters always the same?
          // TODO: meaning of Active=true|false
          
          Map<String,String> params = new HashMap<>();
          for(EntityParameter param:entityP.getParams()){
//            d("    > parameter: %s = %s", param.getName(), param.getValue());
        	  if(param.getName().equalsIgnoreCase("Active") && param.getValue().equalsIgnoreCase("true")){
        		  isActive = true;
        	  }
//
          }
          
          if (! components.contains(payload) && isActive) {
              components.add(new PayloadComponent(payload.getName()));
            }
          isActive = false;
        }
      }
      d("Payload components: %s", components);

    }

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
