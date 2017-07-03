package pt.lsts.nvl.imc;

import java.util.concurrent.atomic.AtomicInteger;

import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.nvl.runtime.EnvironmentException;
import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.tasks.CompletionState;
import pt.lsts.nvl.runtime.tasks.PlatformTaskExecutor;
import pt.lsts.nvl.util.Variable;

public abstract class AbstractIMCPlanExecutor extends PlatformTaskExecutor {

  private static final double WARMUP_TIME = 3.0;
  private static final double PLAN_CONTROL_STATE_TIMEOUT = 20.0;
  private static final AtomicInteger SEQ_ID_GENERATOR = new AtomicInteger(0);

  private Variable<PlanControlState> pcsVar; 

  protected AbstractIMCPlanExecutor(AbstractIMCPlanTask theTask) {
    super(theTask);

  }

  protected abstract void sendMessageToVehicle(IMCMessage msg);
  protected abstract void setup();
  protected abstract void teardown();

  protected final Node getNode() {
    return getVehicles().get(0);
  }

  @Override
  protected final void  onStart() {
    pcsVar = new Variable<>();
    setup(); // platform specific setup

    // Prepare plan control message and send it to vehicle
    PlanControl pc = new PlanControl();
    pc.setPlanId(getTask().getId());
    pc.setType(PlanControl.TYPE.REQUEST);
    pc.setOp(PlanControl.OP.START);
    PlanSpecification planSpec = ((AbstractIMCPlanTask) getTask()).getPlanSpecification();
    if (planSpec != null) {
      pc.setArg(planSpec);
    }
    pc.setRequestId(SEQ_ID_GENERATOR.incrementAndGet());
    pc.setFlags(PlanControl.FLG_CALIBRATE); 
    sendMessageToVehicle(pc);  

    // We're done
    msg("Started"); 
  }

  protected final void onStateUpdate(PlanControlState pcs) {
    pcsVar.set(pcs, timeElapsed());
  }

  @Override
  protected CompletionState onStep() {
    CompletionState completionState =  new CompletionState(CompletionState.Type.IN_PROGRESS);
    if (timeElapsed() > WARMUP_TIME) {
      d("pcsVar: fresh %b age %f ", pcsVar.hasFreshValue(), pcsVar.age(timeElapsed()));
      if (! pcsVar.hasFreshValue()) {
        if (pcsVar.age(timeElapsed()) >= PLAN_CONTROL_STATE_TIMEOUT) {
          msg("PlanControlState timeout!");
          completionState = new CompletionState(CompletionState.Type.ERROR);
        }
      } else {
        PlanControlState pcs = pcsVar.get();
        if (!getTask().getId().equals(pcs.getPlanId())) {
          completionState = new CompletionState(CompletionState.Type.ERROR);
          msg("Wrong plan id reported: " + pcs.getPlanId());
        } else {
          switch (pcs.getState()) {
            case BLOCKED:
              d("ERROR");
              completionState = new CompletionState(CompletionState.Type.ERROR);
              break;
            case EXECUTING:
            case INITIALIZING:
              break;
            case READY:
              switch (pcs.getLastOutcome()) {
                case FAILURE:
                case NONE:
                  completionState = new CompletionState(CompletionState.Type.ERROR);
                  msg("Completed in failure!");
                  break;
                case SUCCESS:
                  completionState = new CompletionState(CompletionState.Type.DONE);
                  msg("Completed successfully!");
                  break;
                default:
                  msg("Unexpected outcome " + pcs.getLastOutcome());
                  throw new EnvironmentException();
              }
              break;
            default:
              throw new EnvironmentException();
          }
        }
      }
    }
    return completionState;
  }

  @Override
  protected final void onCompletion() {
    //PlanControlState pcs = pcsVar.get();
    PlanControl pc = new PlanControl();
    pc.setPlanId(getTask().getId());
    pc.setType(PlanControl.TYPE.REQUEST);
    pc.setOp(PlanControl.OP.STOP);
    msg("Clean-up after plan execution ...");
    teardown();
  }
  
  private void msg(String text) {
    super.msg("%s running %s : %s", getNode().getId(), getTask().getId(), text);
  }

}
