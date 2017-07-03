package pt.lsts.nvl.dsl

import java.util.Map
import java.util.Collections

import groovy.lang.Closure
import pt.lsts.nvl.runtime.*
import pt.lsts.nvl.runtime.tasks.*

import pt.lsts.nvl.util.Debuggable

// DSL instructions
@DSLClass
class Instructions implements Debuggable {


  static void setConnectionTimeout(double timeout) {
    AbstractNode.setDefaultConnectionTimeout timeout
  }

  static void setConnectionTimeout(Map<String,Double> map) {
    for (def e : map) {
      def v = Engine.getInstance().bindingFor e.key
      if (! (v instanceof NodeSet)) {
        halt '\'' + e.key + '\' does not identify a node set'
      }
      for (Node n : (NodeSet) v) {
        n.setConnectionTimeout e.value
      }
    }
  }

  static void message(Object obj) {
    message obj?.toString()
  }

  static void message(String message) {
    Engine.msg("Program message: %s", message)
  }

  static def ask(String prompt) {
    Engine.getInstance().ask(prompt)
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

  static Task idle(double duration) {
    new IdleTask(duration)
  }

  static Task during(double duration, Task task) {
    new TimeConstrainedTask(task, duration)
  }

  static NodeSet pick (Closure cl) {
    new Picker().build(cl)
  }

  static void post(Map signals) {
    Engine.msg 'Posting \'%s\'', signals
    Engine.getInstance().getSignalSet().post(signals)
  }

  static boolean consume(Map signals) {
    boolean b = Engine.getInstance().getSignalSet().consume(signals)
    if (b) Engine.msg 'Consumed \'%s\'', signals
    b
  }

  static boolean poll(Map conditions) {
    Engine.getInstance().getSignalSet().poll(conditions)
  }

  static ActionTask action(Closure cl) {
    return new ActionTask(cl)
  }

  static ConditionTask condition(Closure<Boolean> cl) {
    return new ConditionTask(cl)
  }

  static def waitFor(Closure<Boolean> test) {
    [then: { Task task -> new ChoiceTask(Collections.singletonList(new TaskGuard(test,task))) } ]
  }


  static Position location(double lat, double lon, double height=0) {
    Position.fromDegrees lat, lon, height
  }

  static Area area(Position center, double radius) {
    new Area(center, radius)
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

  static ChoiceTask choose(Closure cl) {
    new ChoiceTaskBuilder().build(cl)
  }

  static GuardedTaskSet allOf(Closure cl) {
    new GuardedTaskSetBuilder().build(cl)
  }

  static def execute(Map<String,Task> map) {
    Task composedTask = idle 0

    for (def e : map) {
      Task t = null
      if (e.key == Engine.WILDCARD) {
        t = e.value
      } else {
        def v = Engine.getInstance().bindingFor e.key
        if (! (v instanceof NodeSet)) {
          halt '\'' + e.key + '\' does not identify a node set'
        }
        t = new ResourceExplicitTask (e.value, (NodeSet) v)
      }
      composedTask = new ConcurrentTaskComposition(composedTask, t)
    }
    execute composedTask
  }

  private Instructions() {

  }

}
