package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;

public abstract class ConstrainedTaskExecutor extends TaskExecutor {

  private  TaskExecutor exec;
  
  public ConstrainedTaskExecutor(Task theTask) {
    super(theTask);
  }

  protected abstract boolean terminationCondition(); 
  
  @Override
  protected CompletionState onStep() {
    return terminationCondition() ?
        new CompletionState(CompletionState.Type.DONE)
       : exec.step();
  }

  @Override
  protected void onInitialize(Map<Task,List<NVLVehicle>> allocation) {
    exec = getTask().getExecutor();
    exec.initialize(allocation);
  }

  @Override
  protected void onStart() {
    exec.start();
  }

  @Override
  protected void onCompletion() {
    exec.onCompletion();
  }
}
