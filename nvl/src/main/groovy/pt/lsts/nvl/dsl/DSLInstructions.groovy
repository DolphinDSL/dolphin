package pt.lsts.nvl.dsl

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import pt.lsts.nvl.runtime.tasks.TaskExecutor
import pt.lsts.nvl.util.Debuggable
import pt.lsts.nvl.runtime.NVLRuntime
import pt.lsts.nvl.runtime.tasks.IdleTask
import pt.lsts.nvl.runtime.tasks.Task

// DSL instructions
@CompileStatic
@TypeChecked
class DSLInstructions {
  static def select(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Selection) Closure cl) {
    new Selection().buildAndExecute(cl)
  }
  
  static TaskBuilder task(String id) {
    new TaskBuilder(id)
  }
  
  static TaskBuilder idle() {
    new TaskBuilder(new IdleTask())
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
