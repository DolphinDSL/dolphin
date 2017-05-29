package pt.lsts.nvl.runtime.tasks;


import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.NVLVehicleSet;

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
  public boolean allocate(NVLVehicleSet available, Map<Task, List<NVLVehicle>> allocation) {
    return true;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new TaskExecutor(this) {

      @Override
      protected void onInitialize(Map<Task,List<NVLVehicle>> allocation) {
      }

      @Override
      protected void onStart() {
      }

      @Override
      protected CompletionState onStep() {
        action.run();
        return new CompletionState(CompletionState.Type.DONE);
      }

      @Override
      protected void onCompletion() {
        
      }
      
    };
  }




}