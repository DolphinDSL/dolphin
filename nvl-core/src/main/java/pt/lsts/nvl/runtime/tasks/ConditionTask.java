package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.NVLVehicleSet;

public class ConditionTask implements Task {

  private final Supplier<Boolean> theGuard;
  
  public ConditionTask(Supplier<Boolean> guard) {
    theGuard = guard;

  }

  @Override
  public final String getId() {
    return "<condition>";
  }

  @Override
  public final boolean allocate(NVLVehicleSet available, Map<Task, List<NVLVehicle>> allocation) {
    return true;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new SimpleTaskExecutor(this) {
      @Override
      protected CompletionState onStep() {
        return new CompletionState(theGuard.get() ? CompletionState.Type.DONE : CompletionState.Type.IN_PROGRESS);
      }
    };
  }
}
