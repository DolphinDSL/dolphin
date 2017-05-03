package pt.lsts.nvl.runtime.tasks;

import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;

public class SequentialTaskComposition implements Task {

  private final Task first;
  private final Task second;
  public SequentialTaskComposition(Task a, Task b) {
    first = a;
    second = b;
  }

  @Override
  public String getId() {
    return first.getId() + " >> " + second.getId();
  }

  @Override
  public void getRequirements(List<VehicleRequirements> requirements) {
     first.getRequirements(requirements);
     second.getRequirements(requirements);
  }

  @Override
  public TaskExecutor getExecutor() {
    final TaskExecutor firstTaskExec = first.getExecutor();
    final TaskExecutor secondTaskExec = second.getExecutor();
    return new TaskExecutor(this) {
      boolean firstTaskDone = false;
      @Override
      protected void onInitialize() {
        firstTaskExec.initialize(null);    
        secondTaskExec.initialize(null);
      }

      @Override
      protected void onStart() {
        firstTaskExec.start();        
      }

      @Override
      protected CompletionState onStep() {
        CompletionState cs;
        if (!firstTaskDone) {
          cs = firstTaskExec.step();;
          if (cs.completed()) {
            firstTaskDone = true;
            secondTaskExec.start();
            cs = secondTaskExec.step();
          }          
        } else {
          cs = secondTaskExec.step();
        }
        return cs;
      }

      @Override
      protected void onCompletion() {

      }
    };
  }

}
