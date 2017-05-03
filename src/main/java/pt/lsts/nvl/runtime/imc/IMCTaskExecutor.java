package pt.lsts.nvl.runtime.imc;

import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.VehicleState;
import pt.lsts.nvl.runtime.NVLExecutionException;
import pt.lsts.nvl.runtime.tasks.CompletionState;
import pt.lsts.nvl.runtime.tasks.PlatformTaskExecutor;
import pt.lsts.nvl.util.Variable;

public final class IMCTaskExecutor extends PlatformTaskExecutor {

  private final static double WARMUP_TIME = 2.0;

  private Variable<PlanControlState> pcsVar; 

  public IMCTaskExecutor(IMCTask theTask) {
    super(theTask);
  }

  private IMCVehicle getVehicle() {
    return (IMCVehicle) getVehicles().get(0);
  }

  @Override
  protected void onStart() {
    IMCVehicle vehicle = getVehicle();    
    PlanControl pc = new PlanControl();
    pc.setPlanId(getTask().getId());
    pc.setType(PlanControl.TYPE.REQUEST);
    pc.setOp(PlanControl.OP.START);
    pc.setFlags(PlanControl.FLG_CALIBRATE); 

    vehicle.send(pc);
    pcsVar = vehicle.subscribe(PlanControlState.class);
  }

  @Override
  protected CompletionState onStep() {
    CompletionState cs = new CompletionState(CompletionState.Type.IN_PROGRESS);
    if (!pcsVar.hasFreshValue()) {
      return cs;
    }

    PlanControlState pcs = pcsVar.get();
    if (!getTask().getId().equals(pcs.getPlanId())) {
      if (clock() > WARMUP_TIME) {
        cs = new CompletionState(CompletionState.Type.ERROR);
      }
    } else {
      switch (pcs.getState()) {
        case BLOCKED:
          cs = new CompletionState(CompletionState.Type.ERROR);
          break;
        case EXECUTING:
        case INITIALIZING:
          break;
        case READY:
          switch (pcs.getLastOutcome()) {
            case FAILURE:
            case NONE:
              cs = new CompletionState(CompletionState.Type.ERROR);
              //log().message("vehicle FAIL");
              break;
            case SUCCESS:
              cs = new CompletionState(CompletionState.Type.DONE);
              break;
            default:
              throw new NVLExecutionException();
          }
          break;
        default:
          throw new NVLExecutionException();
      }
    }
    return cs;
  }

  @Override
  protected void onCompletion() {

  }

}
