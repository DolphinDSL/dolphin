package pt.lsts.nvl.dsl

import pt.lsts.nvl.runtime.NVLVehicleSet

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