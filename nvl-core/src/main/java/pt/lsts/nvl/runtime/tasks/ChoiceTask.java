package pt.lsts.nvl.runtime.tasks;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.NodeSet;

public class ChoiceTask implements Task {

  private final List<TaskGuard> taskGuards;

  public ChoiceTask(List<TaskGuard> list) {
    taskGuards = list;
  }

  @Override
  public String getId() {
    return "choice";
  }

  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) { 
      Map<Task,TaskExecutor> executors = new IdentityHashMap<>();
      TaskExecutor chosen = null;

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
        if (chosen == null) {
          for (TaskGuard tg : taskGuards) {
            if (tg.test()) {
              chosen = executors.get(tg.getTask());
              chosen.start();
              cs = chosen.step();
              break;
            }
          }
        } else {
          cs = chosen.step();
        }
        return cs;
      }
    };
  }

  @Override
  public boolean allocate(NodeSet available,
      Map<Task, List<Node>> allocation) {
    for (TaskGuard tg : taskGuards) {
      if (!tg.getTask().allocate(available, allocation)) {
        return false;
      }
    }
    return true;
  }

}
