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
       : delegate.onStep();
  }

  @Override
  protected void onInitialize() {
    delegate.onInitialize();
    
  }

  @Override
  protected void onStart() {
    delegate.onStart();
    
  }

  @Override
  protected void onCompletion() {
    delegate.onCompletion();
  }


}
