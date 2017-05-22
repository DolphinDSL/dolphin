package pt.lsts.nvl.dsl;

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import java.util.List;
import pt.lsts.nvl.runtime.VehicleRequirements
import pt.lsts.nvl.runtime.tasks.ConcurrentTaskComposition
import pt.lsts.nvl.runtime.tasks.ConstrainedTask
import pt.lsts.nvl.runtime.tasks.ConstrainedTaskExecutor
import pt.lsts.nvl.runtime.tasks.PlatformTask
import pt.lsts.nvl.runtime.tasks.SequentialTaskComposition
import pt.lsts.nvl.runtime.tasks.Task
import pt.lsts.nvl.runtime.tasks.TimeConstrainedTask
import pt.lsts.nvl.util.Debuggable
import pt.lsts.nvl.runtime.tasks.IdleTask

@DSLClass
final class TaskBuilder extends Instruction<Void> implements Debuggable {
  Task task

  TaskBuilder(String id) {
    task = NVLEngine.getInstance().getRuntime().getPlatform().getPlatformTask(id)
  }

  TaskBuilder(Task t) {
    task = t
  }

  TaskBuilder during(double duration) {
    new TaskBuilder ( new TimeConstrainedTask(task, duration) )
  }

  TaskBuilder until(Closure<Boolean> condition) {
    new TaskBuilder ( 
       new ConstrainedTask(task) {
          @Override
          public ConstrainedTaskExecutor getExecutor() {
            return new ConstrainedTaskExecutor(task) {
                  @Override
                  public boolean terminationCondition() {
                    condition.call()
                  }
                }
          }
        }
        )
  }

  TaskBuilder rightShift(TaskBuilder tb) {
    new TaskBuilder ( new SequentialTaskComposition(task, tb.getTask()) )
  }

  TaskBuilder or(TaskBuilder tb) {
    new TaskBuilder ( new ConcurrentTaskComposition(task, tb.getTask()) )
  }

  @Override
  public Void execute() {

  }


}

