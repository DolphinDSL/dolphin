package pt.lsts.nvl.runtime.tasks;

public abstract class ConstrainedTaskExecutor extends TaskExecutor {

  private final TaskExecutor delegate;
  
  public ConstrainedTaskExecutor(Task theTask) {
    super(theTask);
    delegate = theTask.getExecutor();
  }

  protected abstract boolean terminationCondition(); 
  
  @Override
  protected CompletionState onStep() {
    return terminationCondition() ?
        new CompletionState(CompletionState.Type.DONE)
       : delegate.step();
  }

  @Override
  protected void onInitialize() {
    delegate.initialize(null);
  }

  @Override
  protected void onStart() {
    delegate.start();
  }

  @Override
  protected void onCompletion() {
    
  }
}
