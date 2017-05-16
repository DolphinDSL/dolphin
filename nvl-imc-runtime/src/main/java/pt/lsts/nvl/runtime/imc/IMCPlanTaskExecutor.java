package pt.lsts.nvl.runtime.imc;

import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControlState;
import pt.lsts.nvl.imc.AbstractIMCPlanExecutor;
import pt.lsts.nvl.runtime.imc.IMCVehicle.Subscriber;

public final class IMCPlanTaskExecutor extends AbstractIMCPlanExecutor {

  private final Subscriber<PlanControlState> 
    pcsSub = this::onStateUpdate;
 
  public IMCPlanTaskExecutor(IMCPlanTask theTask) {
    super(theTask);
  }

  @Override
  protected void sendMessageToVehicle(IMCMessage msg) {
     ((IMCVehicle) getVehicle()).send(msg);    
  }

  @Override
  protected void setup() {
    ((IMCVehicle) getVehicle())
      .subscribe(PlanControlState.class, pcsSub);
  }

  @Override
  protected void teardown() {
    ((IMCVehicle) getVehicle())
      .unsubscribe(PlanControlState.class, pcsSub);
 
  }

}
