package pt.lsts.nvl.dsl

import java.util.function.Predicate
import pt.lsts.nvl.runtime.*
import pt.lsts.nvl.runtime.tasks.*

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
  Task t -> new ConcurrentTaskComposition(delegate, t)
}

Task.metaClass.rightShift << {
  Task t -> new SequentialTaskComposition(delegate, t)
}

Task.metaClass.getAt << {
  NodeSet set -> new ResourceExplicitTask(delegate, set)
}

// Position & area operators
Position.metaClasss.xor << { 
  double radius -> new Area(delegate, radius)
}
