package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public abstract class SimpleTaskExecutor extends TaskExecutor {

  protected SimpleTaskExecutor(Task theTask) {
    super(theTask);
  }

  @Override
  protected void onInitialize(Map<Task, List<Node>> allocation) { }
    
  @Override
  protected void onStart() { }


  @Override
  protected abstract CompletionState onStep();

  @Override
  protected void onCompletion() { }
}
