package pt.lsts.nvl.dsl

// Distance 
Number.metaClass.getMeters = { -> delegate }
Number.metaClass.getKilometers = { -> delegate * 1000 }

// Angles
Number.metaClass.getRadians = { -> delegate * 180.0 / Math.PI }
Number.metaClass.getDegrees = { -> delegate }

// Orientation
Number.metaClass.getNorth = { -> delegate  }
Number.metaClass.getSouth = { -> - delegate  }
Number.metaClass.getEast = { -> delegate }
Number.metaClass.getWest = { -> -delegate }

// Time
Number.metaClass.getSeconds = { -> delegate }
Number.metaClass.getMinutes = { -> delegate * 60 }
Number.metaClass.getHours = { -> delegate * 3600 }
Number.metaClass.getDays = { -> delegate * 86400 }

// Percentage
Number.metaClass.getPercent = { -> delegate * 0.01 }