package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

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
  public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
    return first.allocate(available, allocation) && second.allocate(available, allocation);
  }

  @Override
  public TaskExecutor getExecutor() {
    final TaskExecutor firstTaskExec = first.getExecutor();
    final TaskExecutor secondTaskExec = second.getExecutor();
    return new TaskExecutor(this) {
      boolean firstTaskCompleted = false,
              secondTaskCompleted = false;
          
      @Override
      protected void onInitialize(Map<Task,List<Node>> allocation) {
        firstTaskExec.initialize(allocation);    
        secondTaskExec.initialize(allocation);
      }

      @Override
      protected void onStart() {
        firstTaskExec.start(); 
        secondTaskExec.start();        
      }

      @Override
      protected CompletionState onStep() {
        if (!firstTaskCompleted) {
          firstTaskCompleted = firstTaskExec.step().finished();
        }
        if (!secondTaskCompleted) {
          secondTaskCompleted = secondTaskExec.step().finished();
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
