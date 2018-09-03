package pt.lsts.dolphin.runtime.tasks;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public class ChoiceTask extends GuardedTaskSet{


  public ChoiceTask(List<TaskGuard> list) {
    super(list);
  }

  @Override
  public String getId() {
    return "<choice>";
  }

  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) { 
      Map<Task,TaskExecutor> executors = new IdentityHashMap<>();
      TaskExecutor chosen = null;

      @Override
      protected void onInitialize(Map<Task, List<Node>> allocation) {
        for (TaskGuard tg : getTaskGuards()) { 
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
          for (TaskGuard tg : getTaskGuards()) {
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
      @Override
      protected void onCompletion() {
          if(chosen!=null)
              chosen.stop();
          else {// stop taskGuards 
              for (TaskGuard tg : getTaskGuards()) {
                  if (tg.test()) {
                      executors.get(tg.getTask()).stop();
                  }
              }
          }
      }
    };
  }


}
