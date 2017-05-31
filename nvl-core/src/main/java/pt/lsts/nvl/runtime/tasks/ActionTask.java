package pt.lsts.nvl.runtime.tasks;


import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.NodeSet;

public class ActionTask implements Task {
  
  private final Runnable action;
  
  public ActionTask(Runnable action) {
    this.action = action;
  }
  
  @Override
  public String getId() {
    return "<action>";
  }

  
  @Override
  public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
    return true;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) {
      @Override
      protected CompletionState onStep() {
        action.run();
        return new CompletionState(CompletionState.Type.DONE);
      }
    };
  }




}