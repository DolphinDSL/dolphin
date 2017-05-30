package pt.lsts.nvl.dsl

import java.util.function.Predicate
import pt.lsts.nvl.runtime.*
import pt.lsts.nvl.runtime.tasks.*

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

NVLVehicleSet.metaClass.or << {
  Closure p -> NVLVehicleSet.subset(delegate, (Predicate<NVLVehicle>) p)
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
