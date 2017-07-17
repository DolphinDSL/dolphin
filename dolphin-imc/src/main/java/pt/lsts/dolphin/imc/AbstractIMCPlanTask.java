package pt.lsts.dolphin.imc;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pt.lsts.dolphin.runtime.EnvironmentException;
import pt.lsts.dolphin.runtime.NodeFilter;
import pt.lsts.dolphin.runtime.Payload;
import pt.lsts.dolphin.runtime.PayloadComponent;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.tasks.PlatformTask;
import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.Goto;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.SetEntityParameters;

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
  public final Optional<Position> getReferencePosition() {
    Optional<PlanManeuver> startManeuver = 
       planSpec.getManeuvers()
               .stream()
               .filter(x -> x.getManeuverId().equals(planSpec.getStartManId()))
               .findFirst();
    if (!startManeuver.isPresent()) {
      throw new EnvironmentException("Plan has no start maneuver!");
    }
    IMCMessage actualManeuver = startManeuver.get().getData();
    if (actualManeuver instanceof Goto) {
      Goto g = (Goto) actualManeuver;
      return Optional.of(new Position(g.getLat(), g.getLon(), 0));
    }
    return Optional.empty();
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
