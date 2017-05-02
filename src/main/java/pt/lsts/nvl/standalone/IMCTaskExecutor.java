package pt.lsts.nvl.standalone;

import pt.lsts.imc.PlanControl;
import pt.lsts.nvl.runtime.tasks.CompletionState;
import pt.lsts.nvl.runtime.tasks.TaskExecutor;

public final class IMCTaskExecutor extends TaskExecutor {

  public IMCTaskExecutor(IMCTask theTask) {
    super(theTask);
  }

  @Override
  protected void onInitialize() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void onStart() {
    PlanControl pc = new PlanControl();
    pc.setPlanId(getTask().getId());
    pc.setType(PlanControl.TYPE.REQUEST);
    pc.setOp(PlanControl.OP.START);
    pc.setFlags(PlanControl.FLG_CALIBRATE); 
  }

  @Override
  protected CompletionState onStep() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void onCompletion() {
    // TODO Auto-generated method stub

  }

}
