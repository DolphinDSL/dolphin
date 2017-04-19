package pt.lsts.nvl.dsl

// Unit conversions for numbers

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
