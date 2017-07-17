package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public abstract class ConstrainedTask implements Task {

  protected final Task theTask;

  public ConstrainedTask(Task t) {
    theTask = t;
  }

  @Override
  public final String getId() {
    return theTask.getId();
  }

  @Override
  public final boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
    return theTask.allocate(available, allocation);
  }
  
  @Override
  public abstract ConstrainedTaskExecutor getExecutor();
}
