//package pt.lsts.nvl.dsl;
//
//import java.nio.channels.spi.AbstractSelectionKey
//import java.util.List;
//import pt.lsts.nvl.runtime.NVLVehicleSet
//import pt.lsts.nvl.runtime.VehicleRequirements
//
//@DSLClass
//final class Selection extends Builder<Boolean> {
//  double time = 0
//  Map<String,VehicleRequirements> req = [:]
//  Closure success
//  Closure failure = { DSLInstructions.halt 'vehicle selection failed' }
//  
//  void time (double arg) {
//    time = arg;
//  }
//  
//  void vehicles(Map<String,Closure> args) {
//    args.each { 
//      name, cl -> vehicle name, cl
//    };
//  }
//  
//  void vehicle(String name, @DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=VehicleRequirementsBuilder) Closure cl) {
//    req[name] = new VehicleRequirementsBuilder().build(cl)
//  }
//
//  void then(Closure cl) {
//    success = cl
//  }
//  
//  void otherwise(Closure cl) {
//    failure = cl
//  }
//  
//  @Override
//  public Boolean build() {
//    Map<String, NVLVehicleSet> choice = [:]
//    if ( NVLEngine.getInstance().getRuntime().select (time, req, choice )) {
//      for (e in choice) {
//        def id = e.key
//        def vs = e.value
//        d '%s --> %s', id, vs
//        NVLEngine.getInstance().bind id, vs
//      }
//      
//      if (success != null) {
//        println 'OWNER' + success.getOwner().getClass() + ' ' + success.getOwner().getProperties() 
//        println 'CHOICE' + choice
//         Map map = [*:choice]
//        for (e in success.getOwner().getProperties()) {
//          map.put e.key, e.value
//        }
//        
//        success.setDelegate map
//        success.setResolveStrategy Closure.DELEGATE_FIRST
//        success.call()
//  
//        for (e in choice) {
//          NVLEngine.getInstance().unbind(e.key)
//        }
//      }
//      true
//    } else {
//      failure?.call()
//      false
//    }
//    
//  }
//  
//}
//
