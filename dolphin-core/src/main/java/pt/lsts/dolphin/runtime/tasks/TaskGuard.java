package pt.lsts.dolphin.runtime.tasks;

import java.util.function.Supplier;

public final class TaskGuard {

  private final Supplier<Boolean> test;
  private final Task task;

  public TaskGuard(Supplier<Boolean> test, Task task) {
    this.test = test;
    this.task = task;
  }

  public Task getTask() {
    return task;
  }
  
  public boolean test() {
    return test.get();
  }

}
