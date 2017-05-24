package pt.lsts.nvl.dsl;

import java.util.List;
import pt.lsts.nvl.runtime.NVLVehicleSet
import pt.lsts.nvl.runtime.VehicleRequirements

@DSLClass
final class Selection extends Instruction<Boolean> {
  double time = 0
  Map<String,VehicleRequirements> req = [:]
  Closure success
  Closure failure
  
  void time (double arg) {
    time = arg;
  }
  
  void vehicles(Map<String,Closure> args) {
    args.each { 
      name, cl -> vehicle name, cl
    };
  }
  
  void vehicle(String name, @DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=VehicleRequirementsBuilder) Closure cl) {
    req[name] = new VehicleRequirementsBuilder().buildAndExecute (cl)
  }

  void then(Closure cl) {
    success = cl
  }
  
  void otherwise(Closure cl) {
    failure = cl
  }
  
  @Override
  public Boolean execute() {
    Map<String, NVLVehicleSet> choice = [:]
    if ( NVLEngine.getInstance().getRuntime().select (time, req, choice )) {
      choice.each {
        id, vs -> NVLEngine.getInstance().bind id, vs
      }
      success?.call()
      true
    } else {
      failure?.call()
      false
    }
    
  }
  
}

