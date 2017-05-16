package pt.lsts.nvl.imc;

import java.util.concurrent.atomic.AtomicInteger;


import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.nvl.runtime.NVLExecutionException;
import pt.lsts.nvl.runtime.NVLVariable;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.tasks.CompletionState;
import pt.lsts.nvl.runtime.tasks.PlatformTaskExecutor;

public abstract class AbstractIMCPlanExecutor extends PlatformTaskExecutor {

  private static final double WARMUP_TIME = 2.0;
  private static final double PLAN_CONTROL_STATE_TIMEOUT = 5.0;
  private static final AtomicInteger SEQ_ID_GENERATOR = new AtomicInteger(0);
  
  private NVLVariable<PlanControlState> pcsVar; 

  protected AbstractIMCPlanExecutor(AbstractIMCPlanTask theTask) {
    super(theTask);
    
  }

  protected abstract void sendMessageToVehicle(IMCMessage msg);
  protected abstract void setup();
  protected abstract void teardown();

  protected final NVLVehicle getVehicle() {
    return getVehicles().get(0);
  }

  @Override
  protected final void  onStart() {
    pcsVar = new NVLVariable<>();
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
    d("Started %s on %s", getTask().getId(), getVehicle().getId());
  }
  
  protected final void onStateUpdate(PlanControlState pcs) {
    pcsVar.set(pcs, timeElapsed());
  }

  @Override
  protected CompletionState onStep() {
      CompletionState completionState =  new CompletionState(CompletionState.Type.IN_PROGRESS);
      if (! pcsVar.hasFreshValue()) {
          if (pcsVar.age(timeElapsed()) >= PLAN_CONTROL_STATE_TIMEOUT) {
              d("PlanControlState timeout!");
              completionState = new CompletionState(CompletionState.Type.ERROR);
          }
      } else {
          PlanControlState pcs = pcsVar.get();
          if (!getTask().getId().equals(pcs.getPlanId())) {
              if (timeElapsed() > WARMUP_TIME) {
                  completionState = new CompletionState(CompletionState.Type.ERROR);
                  d("Wrong plan id: %s != %s", pcs.getPlanId(), getTask().getId());
              }
          } else {
              switch (pcs.getState()) {
                  case BLOCKED:
                      completionState = new CompletionState(CompletionState.Type.ERROR);
                      break;
                  case EXECUTING:
                  case INITIALIZING:
                      break;
                  case READY:
                      d("Terminated %s on %s : %s", getTask().getId(), getVehicle().getId(), pcs.getLastOutcome());
                      switch (pcs.getLastOutcome()) {
                          case FAILURE:
                          case NONE:
                              completionState = new CompletionState(CompletionState.Type.ERROR);
                              d("Failure!");
                              break;
                          case SUCCESS:
                              completionState = new CompletionState(CompletionState.Type.DONE);
                              d("IMC plan completed!");
                              break;
                          default:
                              throw new NVLExecutionException();
                      }
                      break;
                  default:
                      throw new NVLExecutionException();
              }
          }
      }
      return completionState;
  }

  @Override
  protected final void onCompletion() {
    PlanControlState pcs = pcsVar.get();
    if (getTask().getId().equals(pcs.getPlanId()))
    
    teardown();
  }

}
