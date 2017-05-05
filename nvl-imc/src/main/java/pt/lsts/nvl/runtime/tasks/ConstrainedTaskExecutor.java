package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;

public abstract class ConstrainedTaskExecutor extends TaskExecutor {

  private final TaskExecutor task;
  
  public ConstrainedTaskExecutor(Task theTask) {
    super(theTask);
    task = theTask.getExecutor();
  }

  protected abstract boolean terminationCondition(); 
  
  @Override
  protected CompletionState onStep() {
    return terminationCondition() ?
        new CompletionState(CompletionState.Type.DONE)
       : task.step();
  }

  @Override
  protected void onInitialize(Map<Task,List<NVLVehicle>> allocation) {
    task.initialize(allocation);
  }

  @Override
  protected void onStart() {
    task.start();
  }

  @Override
  protected void onCompletion() {
    
  }
}
