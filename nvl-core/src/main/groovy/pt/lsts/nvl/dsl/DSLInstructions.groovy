package pt.lsts.nvl.dsl

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
@CompileStatic
@TypeChecked
class DSLInstructions implements Debuggable {
  static def select(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Selection) Closure cl) {
    new Selection().buildAndExecute(cl)
  }
  
  static TaskBuilder task(String id) {
    new TaskBuilder(id)
  }
  
  static TaskBuilder idle() {
    new TaskBuilder(new IdleTask())
  }
  
  static TaskBuilder during(double duration, Closure<TaskBuilder> cl) {
    new TaskBuilder ( new TimeConstrainedTask(cl.call().getTask(), duration) ) 
  }
  
  static TaskBuilder until(Closure<Boolean> condition, Closure<TaskBuilder> tb) {
    Task t = tb().task
    new TaskBuilder (
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
    })  
  }
  
  static TaskBuilder using(NVLVehicleSet set, Closure<TaskBuilder> tb) {
    println set
    Task t = tb().task
    new TaskBuilder(new ResourceExplicitTask(t, set))
  }
  
  static def execute(TaskBuilder tb) {
    NVLEngine.getInstance().run tb.getTask()
  }
  
  static def execute(Closure<TaskBuilder> cl) {
    execute cl.call()
  }
  
  
  private DSLInstructions() {
    
  }
  
}
