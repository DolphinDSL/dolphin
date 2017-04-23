package pt.lsts.nvl.runtime.tasks;

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
    return null;
  }

}
