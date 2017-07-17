package pt.lsts.dolphin.runtime.tasks;


import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public abstract class  GuardedTaskSet implements Task {

  private final List<TaskGuard> taskGuards;

  public GuardedTaskSet(List<TaskGuard> list) {
    taskGuards = list;
  }

  protected final List<TaskGuard >getTaskGuards() {
    return taskGuards;
  }
  
  @Override
  public String getId() {
    return "<guardedTaskSet>";
  }

  @Override
  public final boolean allocate(NodeSet available,Map<Task, List<Node>> allocation) {
    NodeSet allocated = new NodeSet();
    for (TaskGuard tg : taskGuards) {
      NodeSet ns = available.clone();
      if (!tg.getTask().allocate(ns, allocation)) {
        return false;
      }
      allocated.addAll(NodeSet.difference(available, ns));
      d("allocated: %s", allocated);
    }
    available.removeAll(allocated);
    return true;
  }
  
  @Override
  public abstract TaskExecutor getExecutor();
}
