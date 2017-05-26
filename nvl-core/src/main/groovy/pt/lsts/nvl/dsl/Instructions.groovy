package pt.lsts.nvl.dsl

import pt.lsts.nvl.runtime.*
import pt.lsts.nvl.runtime.tasks.*

import pt.lsts.nvl.util.Debuggable

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
    Engine.runtime().pause duration
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
