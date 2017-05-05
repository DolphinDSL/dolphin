package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;

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
  public boolean allocate(List<NVLVehicle> available, Map<Task, List<NVLVehicle>> allocation) {
    return first.allocate(available, allocation) && second.allocate(available, allocation);
  }

  @Override
  public TaskExecutor getExecutor() {
    final TaskExecutor firstTaskExec = first.getExecutor();
    final TaskExecutor secondTaskExec = second.getExecutor();
    return new TaskExecutor(this) {
      boolean firstTaskDone = false;
      @Override
      protected void onInitialize(Map<Task,List<NVLVehicle>> allocation) {
        firstTaskExec.initialize(allocation);    
        secondTaskExec.initialize(allocation);
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
