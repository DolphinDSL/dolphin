//package pt.lsts.nvl.dsl;
//
//import java.nio.channels.spi.AbstractSelectionKey
//import java.util.List;
//
//import groovy.lang.Closure
//import pt.lsts.nvl.runtime.NVLVehicleSet
//import pt.lsts.nvl.runtime.VehicleRequirements
//import pt.lsts.nvl.util.Debuggable
//
//@DSLClass
//final class LetBlock implements Debuggable {
//  private double time = 1
//  private Map<String,VehicleRequirements> req = [:]
//  private Closure success
//  private Closure failure = { DSLInstructions.halt 'vehicle selection failed' }
//
//  LetBlock(Map args) {
//    args.each {
//      name, cl -> vehicle ((String)name, (Closure) cl)
//    }
//  }
//
//  LetBlock wait (double duration) {
//    time = duration
//    this
//  }
//
//  void then(Closure cl) {
//    dump 'AT ENTRY', cl
//    
//    Map<String, NVLVehicleSet> choice = [:]
//    if ( NVLEngine.getInstance().getRuntime().select (time, req, choice )) {
//      for (e in choice) {
//        def id = e.key
//        def vs = e.value
//        d '%s --> %s', id, vs
//        NVLEngine.getInstance().bind id, vs
//      }
//      Map previouslyDefined = cl.thisObject instanceof Script ?
//                              ((Script) (cl.thisObject)).binding.variables :
//                              (Map) cl.thisObject;
//      d "PRV " + previouslyDefined;
//      Binding binding = (Binding) (cl.owner.getProperties().binding)
//      def closureVars = [*:choice, *:previouslyDefined]
//      d "CLV " + closureVars
//
//      def code = cl.rehydrate closureVars, closureVars, closureVars
//      dump 'REHY', code
//      code.setResolveStrategy Closure.DELEGATE_FIRST
//      code.call()
//    }
//  }
//  
//  def dump(String s, Closure cl) {
//    d "$s - thisObject " + cl.thisObject
//    d "$s - owner " + cl.owner
//    d "$s - delegate " + cl.delegate
//    
////    cl.thisObject.getProperties().each {
////      k,v -> println "THIS $k, $v"
////    }
////    cl.owner.getProperties().each {
////      k,v -> println "OWNER $k, $v"
////    }
////    Binding b = (Binding) (cl.owner.getProperties().binding);
////    Map m = (Map)  b.getProperty('variables')
////    m.each {
////      k,v -> println "DELEGATE '$k' -> '$v'"
////    }
//    
//  }
//
//  LetBlock onError(Closure cl) {
//    failure = cl
//    this
//  }
//
// 
//
//  //  void time (double arg) {
//  //    time = arg;
//  //  }
//  //
//  //  void vehicles(Map<String,Closure> args) {
//  //    args.each {
//  //      name, cl -> vehicle name, cl
//  //    };
//  //  }
//  //
//
//  //
//  //  void then(Closure cl) {
//  //    success = cl
//  //  }
//  //
//  //  void otherwise(Closure cl) {
//  //    failure = cl
//  //  }
//  //
//  //  @Override
//  //  public Boolean execute() {
//  //    Map<String, NVLVehicleSet> choice = [:]
//  //    if ( NVLEngine.getInstance().getRuntime().select (time, req, choice )) {
//  //      for (e in choice) {
//  //        def id = e.key
//  //        def vs = e.value
//  //        d '%s --> %s', id, vs
//  //        NVLEngine.getInstance().bind id, vs
//  //      }
//  //
//  //      if (success != null) {
//  //        println 'OWNER' + success.getOwner().getClass() + ' ' + success.getOwner().getProperties()
//  //        println 'CHOICE' + choice
//  //         Map map = [*:choice]
//  //        for (e in success.getOwner().getProperties()) {
//  //          map.put e.key, e.value
//  //        }
//  //
//  //        success.setDelegate map
//  //        success.setResolveStrategy Closure.DELEGATE_FIRST
//  //        success.call()
//  //
//  //        for (e in choice) {
//  //          NVLEngine.getInstance().unbind(e.key)
//  //        }
//  //      }
//  //      true
//  //    } else {
//  //      failure?.call()
//  //      false
//  //    }
//  //
//  //  }
//
//}
//
