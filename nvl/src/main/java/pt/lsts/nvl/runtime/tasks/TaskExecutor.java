package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.NVLExecutionException;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.util.Clock;
import pt.lsts.nvl.util.Debuggable;

public abstract class TaskExecutor implements Debuggable {
  /**
   * Task executor state.
   */
  public enum State {
    /** INITIALIZING. */
    INITIALIZING,
    /** READY. */
    READY,
    /** Executing */
    EXECUTING,
    /** Completed */
    COMPLETED;
  }

  private final Task task;
  private State state;
  private double startTime;
  private double timeElapsed;
  private CompletionState completionState;

  protected TaskExecutor(Task theTask) {
    task = theTask;
    state = State.INITIALIZING;
    startTime = -1;
    completionState = new CompletionState(CompletionState.Type.UNDEFINED);
  }

  public final Task getTask() { 
    return task;
  }

  public final State getState() {
    return state;
  }

  public final double startTime() {
    return startTime;
  }

  public final CompletionState getCompletionState() {
    return completionState;
  }
  
 
  public boolean initialize(Map<Task,List<NVLVehicle>> allocation) {
    requireState(State.INITIALIZING);
    onInitialize(allocation);
    state = State.READY;
    return true;    
  }

  public final void start() {
    requireState(State.READY);
    onStart();
    startTime = Clock.now();
    state = State.EXECUTING;
  }

  public final CompletionState step() {
    requireState(State.EXECUTING);
    timeElapsed = Clock.now() - startTime;
    completionState = onStep();
    if (completionState.completed()) {
      state = State.COMPLETED;
      onCompletion();
    }
    return completionState;
  }

  public final double clock() {
    return timeElapsed;
  }

  protected abstract void onInitialize(Map<Task,List<NVLVehicle>> allocation);
  protected abstract void onStart();
  protected abstract CompletionState onStep();
  protected abstract void onCompletion();

  private void requireState(State s) {
    if (getState() != s) {
      throw new NVLExecutionException("Expected " + s + " state");
    }
  }
  
  
 
}
