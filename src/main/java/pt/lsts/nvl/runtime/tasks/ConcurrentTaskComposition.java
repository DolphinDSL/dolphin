package pt.lsts.nvl.runtime.tasks;

import java.util.Collections;
import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public class ConcurrentTaskComposition implements Task {

  private final Task first;
  private final Task second;
  public ConcurrentTaskComposition(Task a, Task b) {
    first = a;
    second = b;
  }

  @Override
  public String getId() {
    return first.getId() + " | " + second.getId();
  }

  @Override
  public List<VehicleRequirements> getRequirements() {
     return Collections.emptyList();
  }

  @Override
  public TaskExecutor getExecutor() {
    final TaskExecutor firstTaskExec = first.getExecutor();
    final TaskExecutor secondTaskExec = second.getExecutor();
    return new TaskExecutor(this) {
      boolean firstTaskCompleted = false,
              secondTaskCompleted = false;
          
      @Override
      protected void onInitialize() {
        firstTaskExec.initialize(null);    
        secondTaskExec.initialize(null);
      }

      @Override
      protected void onStart() {
        firstTaskExec.start(); 
        secondTaskExec.start();        
      }

      @Override
      protected CompletionState onStep() {
        if (!firstTaskCompleted) {
          firstTaskCompleted = firstTaskExec.step().completed();
        }
        if (!secondTaskCompleted) {
          secondTaskCompleted = secondTaskExec.step().completed();
        }
        return firstTaskCompleted && secondTaskCompleted ?
              new CompletionState(CompletionState.Type.DONE)
            :  new CompletionState(CompletionState.Type.IN_PROGRESS);
      }

      @Override
      protected void onCompletion() {

      }
    };
  }

}
