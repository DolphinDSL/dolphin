package pt.lsts.nvl.dsl

import pt.lsts.nvl.runtime.NVLVehicleSet
import pt.lsts.nvl.runtime.tasks.ConcurrentTaskComposition
import pt.lsts.nvl.runtime.tasks.ResourceExplicitTask
import pt.lsts.nvl.runtime.tasks.SequentialTaskComposition
import pt.lsts.nvl.runtime.tasks.Task


// Vehicle set operators
NVLVehicleSet.metaClass.plus << {
  NVLVehicleSet other -> NVLVehicleSet.union(delegate, other)
}

NVLVehicleSet.metaClass.minus << {
  NVLVehicleSet other -> NVLVehicleSet.difference(delegate, other)
}

NVLVehicleSet.metaClass.and << {
  NVLVehicleSet other -> NVLVehicleSet.intersection(delegate, other)
}

NVLVehicleSet.metaClass.leftShift << {
  Closure<TaskBuilder> ctb -> DSLInstructions.using(delegate, ctb) 
} 

// Task operators

Task.metaClass.or << {
  Task t -> new ConcurrentTaskComposition(delegate, t)
}

Task.metaClass.rightShift << {
  Task t -> new SequentialTaskComposition(delegate, t)
}

Task.metaClass.getAt << {
  NVLVehicleSet set -> new ResourceExplicitTask(delegate, set)
}
