package pt.lsts.nvl.runtime.tasks;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.NodeSet;

public class GuardedTaskSet implements Task {

  private final List<TaskGuard> taskGuards;

  public GuardedTaskSet(List<TaskGuard> list) {
    taskGuards = list;
  }

  @Override
  public String getId() {
    return "<guardedTaskSet>";
  }

  @Override
  public boolean allocate(NodeSet available,Map<Task, List<Node>> allocation) {
    NodeSet aux = available.clone();
    for (TaskGuard tg : taskGuards) {
      NodeSet ns = available.clone();
      if (!tg.getTask().allocate(ns, allocation)) {
        return false;
      }
      aux.addAll(NodeSet.intersection(aux, ns));
    }
    available.clear();
    available.addAll(aux);
    return true;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) { 
      Map<Task,TaskExecutor> executors = new IdentityHashMap<>();
      TaskExecutor current = null;

      @Override
      protected void onInitialize(Map<Task, List<Node>> allocation) {
        for (TaskGuard tg : taskGuards) { 
          Task t = tg.getTask();
          TaskExecutor exec = t.getExecutor();
          executors.put(tg.getTask(), exec);
          exec.initialize(allocation);
        }
      }

      @Override
      protected CompletionState onStep() {
        CompletionState cs = new CompletionState(CompletionState.Type.IN_PROGRESS);
        if (current == null) {
          for (TaskGuard tg : taskGuards) {
            if (executors.containsKey(tg.getTask()) && tg.test()) {
              current = executors.remove(tg.getTask());
              current.start();
              cs = current.step();
              break;
            }
          }
        } else {
          cs = current.step();
        }
        
        if (cs.completed()) {
          if (!executors.isEmpty()) {
            cs = new CompletionState(CompletionState.Type.IN_PROGRESS);
            current = null;
          }
        }
        return cs;
      }
    };
  }

 

}
