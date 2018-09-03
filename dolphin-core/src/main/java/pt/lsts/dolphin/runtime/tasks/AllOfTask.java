package pt.lsts.dolphin.runtime.tasks;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public class AllOfTask extends GuardedTaskSet {

  public AllOfTask(List<TaskGuard> list) {
    super(list);
  }

  @Override
  public String getId() {
    return "<allOf>";
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) { 
      Map<Task,TaskExecutor> executors = new IdentityHashMap<>();
      TaskExecutor current = null;

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
        if (current == null) {
          for (TaskGuard tg : getTaskGuards()) {
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
        
        if (cs.done()) {
          if (!executors.isEmpty()) {
            cs = new CompletionState(CompletionState.Type.IN_PROGRESS);
            current = null;
          }
        }
        else if(cs.error()){ //same behavior as ConcurentTaskExecutor
            return cs;
        }
        return cs;
      }
      @Override
        protected void onCompletion() {
          if(current!=null)
              current.stop();
          for(TaskExecutor taskExec: executors.values()){
              taskExec.stop();
          }
          executors.clear();
        }
    };
  }

 

}
