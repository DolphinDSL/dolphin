package pt.lsts.dolphin.runtime.imc;

import pt.lsts.dolphin.imc.AbstractIMCPlanExecutor;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControlState;

public final class IMCPlanTaskExecutor extends AbstractIMCPlanExecutor {
 
  public IMCPlanTaskExecutor(IMCPlanTask theTask) {
    super(theTask);
  }

  @Override
  protected void sendMessageToVehicle(IMCMessage msg) {
     ((IMCNode) getNode()).send(msg);    
  }

  @Override
  protected void setup() {
    IMCCommunications.getInstance()
                     .getMessageHandler()
                     .bind(PlanControlState.class, (n,m) -> { this.onStateUpdate(m); });
  }

  @Override
  protected void teardown() {
    IMCCommunications.getInstance()
                     .getMessageHandler()
                     .unbind(PlanControlState.class);
  }

}
