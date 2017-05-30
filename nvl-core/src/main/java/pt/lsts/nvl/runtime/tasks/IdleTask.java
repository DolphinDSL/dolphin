package pt.lsts.nvl.runtime.tasks;


import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.NVLVehicleSet;

public class IdleTask implements Task {
  
  private final double duration;
  
  public IdleTask(double duration) {
    this.duration = duration;
  }
  
  @Override
  public String getId() {
    return String.format("idle(%f)", duration);
  }

  
  @Override
  public boolean allocate(NVLVehicleSet available, Map<Task, List<NVLVehicle>> allocation) {
    return true;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) {
      @Override
      protected CompletionState onStep() {
        return new CompletionState(timeElapsed() >= duration ? 
                                       CompletionState.Type.DONE 
                                     : CompletionState.Type.IN_PROGRESS);
      }
    };
  }




}