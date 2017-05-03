package pt.lsts.nvl.runtime.tasks;

import static pt.lsts.nvl.util.Debug.d;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLVehicle;

public final class TimeConstrainedTask extends ConstrainedTask {

  private final double duration;
  
  public TimeConstrainedTask(Task task, double duration) {
    super(task);
    this.duration = duration;
  }

  public final double getDuration() {
    return duration;
  }
  
  @Override
  public TaskExecutor getExecutor() {
    return new ConstrainedTaskExecutor(theTask) {
      @Override
      protected boolean terminationCondition() {
        double now = clock();
        d("%s %f >= %f ?", theTask.getId(), now, duration);
        return now >= duration;
      }
    };
  }

 

}
