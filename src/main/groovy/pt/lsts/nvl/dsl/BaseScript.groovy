package pt.lsts.nvl.dsl

import groovy.transform.TypeChecked
import pt.lsts.nvl.runtime.tasks.TaskExecutor
import pt.lsts.nvl.runtime.tasks.Task
import pt.lsts.nvl.util.Debug

Debug.enable()
NumberUnits.main() 

// DSL instructions
@TypeChecked
static def select(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Selection) Closure cl) {
  new Selection().buildAndExecute(cl)
}

@TypeChecked
static TaskBuilder task(String id) {  
  new TaskBuilder(id)
}

@TypeChecked
static def execute(TaskBuilder tb) {
  TaskExecutor.run tb.getTask()
}



