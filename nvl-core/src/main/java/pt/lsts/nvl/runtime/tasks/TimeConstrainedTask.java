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
  public ConstrainedTaskExecutor getExecutor() {
    return new ConstrainedTaskExecutor(theTask) {
      @Override
      protected boolean terminationCondition() {
        double now = timeElapsed();
        // d("%s %f >= %f ?", theTask.getId(), now, duration);
        return now >= duration;
      }
    };
  }
}
