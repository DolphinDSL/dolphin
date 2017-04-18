package pt.lsts.nvl.dsl;

import pt.lsts.nvl.runtime.NVLVehicleType

@DSLClass
final class Requirements extends Instruction {
  NVLVehicleType type;
  String[] payload;

  void type(NVLVehicleType t) {
    type = t;
  }
  
  void payload(String... p) {
    println p
    payload = p;
  }

  @Override
  public void execute() {
    println "e " + toString()
  }
}