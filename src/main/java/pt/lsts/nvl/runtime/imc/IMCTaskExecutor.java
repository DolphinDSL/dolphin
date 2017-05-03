package pt.lsts.nvl.runtime.imc;

import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControlState;
import pt.lsts.nvl.runtime.tasks.CompletionState;
import pt.lsts.nvl.runtime.tasks.PlatformTaskExecutor;

public final class IMCTaskExecutor extends PlatformTaskExecutor {

  public IMCTaskExecutor(IMCTask theTask) {
    super(theTask);
  }

  private IMCVehicle getVehicle() {
    return (IMCVehicle) getVehicles().get(0);
  }
  
  @Override
  protected void onStart() {
    IMCVehicle vehicle = getVehicle();
    IMCCommunications comm = IMCCommunications.getInstance();
    
    PlanControl pc = new PlanControl();
    pc.setPlanId(getTask().getId());
    pc.setType(PlanControl.TYPE.REQUEST);
    pc.setOp(PlanControl.OP.START);
    pc.setFlags(PlanControl.FLG_CALIBRATE); 

    comm.send(vehicle.getId(), pc);
    vehicle.subscribe(PlanControlState.class, this::consume);
  }
  
  private void consume(PlanControlState message) {
    
  }

  @Override
  protected CompletionState onStep() {
    return new CompletionState(CompletionState.Type.IN_PROGRESS);
  }

  @Override
  protected void onCompletion() {

  }

}
