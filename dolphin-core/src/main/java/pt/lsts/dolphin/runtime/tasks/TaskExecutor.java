package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Environment;
import pt.lsts.dolphin.runtime.EnvironmentException;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.util.Clock;
import pt.lsts.dolphin.util.Debuggable;

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
  
 
  public boolean initialize(Map<Task,List<Node>> allocation) {
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
    if (completionState.finished()) {
      state = State.COMPLETED;
      onCompletion();
    }
    return completionState;
  }

  public final double timeElapsed() {
    return timeElapsed;
  }

  protected abstract void onInitialize(Map<Task,List<Node>> allocation);
  protected abstract void onStart();
  protected abstract CompletionState onStep();
  protected abstract void onCompletion();

  
  private void requireState(State s) {
    if (getState() != s) {
      throw new EnvironmentException("Expected " + s + " state");
    }
  }
  
  protected final void msg(String fmt, Object... args) {
    Environment.getInstance().getPlatform().displayMessage(fmt, args);
  }
  
  
 
}
