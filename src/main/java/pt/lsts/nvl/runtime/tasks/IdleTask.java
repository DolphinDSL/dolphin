package pt.lsts.nvl.runtime.tasks;


import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;
import static pt.lsts.nvl.util.Debug.d;

public class IdleTask implements Task {
  
  public IdleTask() {
   
  }
  
  @Override
  public String getId() {
    return "<idle>";
  }

  
  @Override
  public boolean allocate(List<NVLVehicle> available, Map<Task, List<NVLVehicle>> allocation) {
    return true;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new TaskExecutor(this) {

      @Override
      protected void onInitialize(Map<Task,List<NVLVehicle>> allocation) {
        d("Initialized " + getId());     
      }

      @Override
      protected void onStart() {
        d("Started " + getId());
      }

      @Override
      protected CompletionState onStep() {
        return new CompletionState(CompletionState.Type.IN_PROGRESS);
      }

      @Override
      protected void onCompletion() {
        d("Completed " + getId());     
      }
      
    };
  }




}