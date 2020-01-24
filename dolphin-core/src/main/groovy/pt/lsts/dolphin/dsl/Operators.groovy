package pt.lsts.dolphin.dsl

import java.util.function.Predicate
import pt.lsts.dolphin.runtime.*
import pt.lsts.dolphin.runtime.tasks.*

// Vehicle set operators
NodeSet.metaClass.plus << {
  NodeSet other -> NodeSet.union(delegate, other)
}

NodeSet.metaClass.minus << {
  NodeSet other -> NodeSet.difference(delegate, other)
}

NodeSet.metaClass.and << {
  NodeSet other -> NodeSet.intersection(delegate, other)
}

NodeSet.metaClass.or << {
  Closure p -> NodeSet.subset(delegate, (Predicate<Node>) p)
}

// Task operators

Task.metaClass.or << {
  Task t -> new ConcurrentTask(delegate, t)
}

Task.metaClass.div << {
  Closure<Boolean> test ->
    new ConstrainedTask(delegate) {
      @Override
      public ConstrainedTaskExecutor getExecutor() {
        return new ConstrainedTaskExecutor(theTask) {
          @Override
          public boolean terminationCondition() {
            test.call()
          }
        }
      }
    }
}

Task.metaClass.rightShift << {
  Task t -> new SequentialTask(delegate, t)
}

Task.metaClass.getAt << {
  NodeSet set -> new ResourceExplicitTask(delegate, set)
}

// Position & area operators
Position.metaClass.xor << { 
  double radius -> new Area(delegate, radius)
}
