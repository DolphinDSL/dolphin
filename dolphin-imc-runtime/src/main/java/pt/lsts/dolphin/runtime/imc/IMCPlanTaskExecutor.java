package pt.lsts.dolphin.runtime.imc;

import pt.lsts.dolphin.imc.AbstractIMCPlanExecutor;
import pt.lsts.dolphin.runtime.imc.IMCNode.Subscriber;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControlState;

public final class IMCPlanTaskExecutor extends AbstractIMCPlanExecutor {

  private final Subscriber<PlanControlState> 
    pcsSub = this::onStateUpdate;
 
  public IMCPlanTaskExecutor(IMCPlanTask theTask) {
    super(theTask);
  }

  @Override
  protected void sendMessageToVehicle(IMCMessage msg) {
     ((IMCNode) getNode()).send(msg);    
  }

  @Override
  protected void setup() {
    ((IMCNode) getNode())
      .subscribe(PlanControlState.class, pcsSub);
  }

  @Override
  protected void teardown() {
    ((IMCNode) getNode())
      .unsubscribe(PlanControlState.class, pcsSub);
 
  }

}
