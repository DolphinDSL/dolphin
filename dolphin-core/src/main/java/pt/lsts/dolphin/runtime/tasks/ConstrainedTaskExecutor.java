package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

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
  protected void onInitialize(Map<Task,List<Node>> allocation) {
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
