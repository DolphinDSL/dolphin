package pt.lsts.nvl.dsl;

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import java.util.List;
import pt.lsts.nvl.runtime.VehicleRequirements
import pt.lsts.nvl.runtime.tasks.PlatformTask
import pt.lsts.nvl.runtime.tasks.Task
import pt.lsts.nvl.runtime.tasks.TimeConstrainedTask

@DSLClass
final class TaskBuilder extends Instruction<Void> {
  Task task

  TaskBuilder(String id) {
    task = new PlatformTask(id)
  }

  TaskBuilder during(double duration) {
    task = new TimeConstrainedTask(task, duration)
    this
  }

  @Override
  public Void execute() {

  }

  def asType(Class target) {
    if (target != Task) {
      throw new ClassCastException("User cannot be coerced into $target")
    }
    task
  }
}

