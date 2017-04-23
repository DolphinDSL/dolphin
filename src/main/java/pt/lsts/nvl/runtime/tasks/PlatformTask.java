package pt.lsts.nvl.runtime.tasks;

import java.util.Collections;
import java.util.List;

import pt.lsts.nvl.runtime.VehicleRequirements;
import static pt.lsts.nvl.util.Debug.d;

public class PlatformTask implements Task {

  private final String id;
  
  public PlatformTask(String id) {
    this.id = id;
  }
  
  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<VehicleRequirements> getRequirements() {
    return Collections.emptyList();
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new TaskExecutor(this) {

      @Override
      protected void onInitialize() {
        d("Init" + getId());     
      }

      @Override
      protected void onStart() {
        d("Start" + getId());     
        
      }

      @Override
      protected CompletionState onStep() {
        return new CompletionState(CompletionState.Type.IN_PROGRESS);
      }

      @Override
      protected void onCompletion() {
        d("Completed" + getId());     
      }
      
    };
  }


}
