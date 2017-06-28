package pt.lsts.nvl.imc;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

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
	  boolean isActive = false;
      for(PlanManeuver pm: ps.getManeuvers()){
          //pm.getStartActions().get(0).setMessageList(SetEntityParameters, field);
          for(IMCMessage m: pm.getStartActions())
              try {
                  SetEntityParameters payload = SetEntityParameters.clone(m);
                  Map<String,String> params = new HashMap<>();
                  for(EntityParameter param:payload.getParams()){
                	  //System.out.println("Payload "+payload.getName()+" parameter: "+param.getName()+" "+param.getValue());
                	  if(param.getName().equalsIgnoreCase("Active") && param.getValue().equalsIgnoreCase("true"))
                		  isActive = true;
                      params.put(param.getName(),param.getValue());
                  }
                  if(isActive)
                	  components.add(new PayloadComponent(payload.getName(),params));
                  
                  
              }
              catch (Exception e) {
                  // TODO Auto-generated catch block
                  
              }
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
