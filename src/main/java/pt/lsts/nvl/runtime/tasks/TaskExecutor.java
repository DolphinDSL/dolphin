package pt.lsts.nvl.runtime.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pt.lsts.nvl.runtime.NVLRuntime;
import pt.lsts.nvl.runtime.NVLRuntimeException;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.VehicleRequirements;
import pt.lsts.nvl.util.Clock;

public abstract class TaskExecutor<T extends Task> {
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

  private final T task;
  private State state;
  private double startTime;
  private double timeElapsed;
  private List<NVLVehicle> boundVehicles;

  TaskExecutor(T theTask) {
    task = theTask;
    state = State.INITIALIZING;
    startTime = -1;
    boundVehicles = Collections.emptyList();
  }

  public final T getTask() { 
    return task;
  }

  public final State getState() {
    return state;
  }

  public final double startTime() {
    return startTime;
  }

  public final List<NVLVehicle> getVehicles() {
    return boundVehicles; 
  }

  public boolean initialize(NVLRuntime runtime) {
    requireState(State.INITIALIZING);
    onInitialize();
    List<NVLVehicle> reservedVehicles = new ArrayList<>();
    List<NVLVehicle> allVehicles = runtime.getVehicles(x -> true);
    for (VehicleRequirements r : task.getRequirements()) {
      Optional<NVLVehicle> v = allVehicles.stream().filter(veh -> r.apply(veh)).findFirst();
      if (!v.isPresent()) {
        return false;
      }
      reservedVehicles.add(v.get());
    }
    boundVehicles = Collections.unmodifiableList(reservedVehicles);
    state = State.READY;
    return true;    
  }

  public final void start() {
    requireState(State.READY);
    onStart();
    startTime = Clock.now();
    state = State.EXECUTING;
  }

  public final void step() {
    requireState(State.EXECUTING);
    timeElapsed = Clock.now() - startTime;
    onStep();
  }

  public final double clock() {
    return timeElapsed;
  }

  protected abstract void onInitialize();
  protected abstract void onStart();
  protected abstract void onStep();
  protected abstract void onCompletion();

  private void requireState(State s) {
    if (getState() != s) {
      throw new NVLRuntimeException("Expected " + s + " state");
    }
  }
}
