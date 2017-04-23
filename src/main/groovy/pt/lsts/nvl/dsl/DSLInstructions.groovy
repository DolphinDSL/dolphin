package pt.lsts.nvl.dsl

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import pt.lsts.nvl.runtime.tasks.TaskExecutor
import pt.lsts.nvl.runtime.tasks.Task
import static pt.lsts.nvl.util.Debug.d

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
  
  static def execute(TaskBuilder tb) {
    d tb.getTask().getId()
    TaskExecutor.run tb.getTask()
  }
  
  private DSLInstructions() {
    
  }
  
}
