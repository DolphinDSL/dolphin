package pt.lsts.nvl.runtime.tasks;

public abstract class ConstrainedTaskExecutor extends TaskExecutor {

  private final TaskExecutor task;
  
  public ConstrainedTaskExecutor(Task theTask) {
    super(theTask);
    task = theTask.getExecutor();
  }

  protected abstract boolean terminationCondition(); 
  
  @Override
  protected CompletionState onStep() {
    return terminationCondition() ?
        new CompletionState(CompletionState.Type.DONE)
       : task.step();
  }

  @Override
  protected void onInitialize() {
    task.initialize(null);
  }

  @Override
  protected void onStart() {
    task.start();
  }

  @Override
  protected void onCompletion() {
    
  }
}
