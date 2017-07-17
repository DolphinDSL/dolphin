package pt.lsts.dolphin.dsl

import pt.lsts.dolphin.runtime.tasks.*

@DSLClass
class GuardedTaskSetBuilder extends Builder<GuardedTaskSet> {

  private List<TaskGuard> taskGuards = [] 
  
  def when(Closure<Boolean> test) { 
    [then: { Task task -> taskGuards << new TaskGuard(test,task)} ]
  }
  
  @Override
  public GuardedTaskSet build() {
    if (taskGuards.empty) {
      Engine.halt 'No choices were defined!'
    }
    new AllOfTask(taskGuards)
  }
}
