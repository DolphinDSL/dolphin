package pt.lsts.nvl.dsl

import java.util.concurrent.ForkJoinTask.AdaptedCallable

import groovy.lang.Closure
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import pt.lsts.nvl.runtime.tasks.TaskExecutor
import pt.lsts.nvl.runtime.tasks.TimeConstrainedTask
import pt.lsts.nvl.util.Debuggable
import pt.lsts.nvl.runtime.NVLRuntime
import pt.lsts.nvl.runtime.NVLVehicleSet
import pt.lsts.nvl.runtime.tasks.ConstrainedTask
import pt.lsts.nvl.runtime.tasks.ConstrainedTaskExecutor
import pt.lsts.nvl.runtime.tasks.IdleTask
import pt.lsts.nvl.runtime.tasks.ResourceExplicitTask
import pt.lsts.nvl.runtime.tasks.Task

// DSL instructions
@DSLClass
class Instructions implements Debuggable {

  static void message(String message) {
    Engine.msg("Program message: %s", message)
  }

  static void halt(String message='') {
    Engine.halt(message)
  }

  static void pause(double duration) {
    Engine.msg "Pausing for %f s ...", duration
    NVLRuntime.pause duration
  }

  static Task task(String id) {
    Engine.platform().getPlatformTask(id)
  }

  static Task idle() {
    new IdleTask()
  }

  static Task during(double duration, Task task) {
    new TimeConstrainedTask(task, duration)
  }
  
  static NVLVehicleSet pick (Closure cl) {
    new Picker().build(cl)
  }
  
  static Task until(Closure<Boolean> condition, Task t) {
    new ConstrainedTask(t) {
          @Override
          public ConstrainedTaskExecutor getExecutor() {
            return new ConstrainedTaskExecutor(t) {
                  @Override
                  public boolean terminationCondition() {
                    condition.call()
                  }
                }
          }
        }
  }

 
  static def execute(Task t) {
    Engine.getInstance().run t
  }


  private Instructions() {

  }

}
