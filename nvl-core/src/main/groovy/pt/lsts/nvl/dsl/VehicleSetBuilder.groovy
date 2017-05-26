package pt.lsts.nvl.dsl;


import java.util.concurrent.ConcurrentHashMap.ForEachEntryTask
import java.util.concurrent.atomic.DoubleAdder

import pt.lsts.nvl.runtime.NVLVehicleSet
import pt.lsts.nvl.runtime.PayloadComponent
import pt.lsts.nvl.runtime.Position
import pt.lsts.nvl.runtime.VehicleRequirements

@DSLClass
final class VehicleSetBuilder
extends Builder<NVLVehicleSet> {

  VehicleRequirements req = new VehicleRequirements()
  int count = 1
  double timeout = 0
 
  void count(int n) {
    if (n <= 0) {
      DSLInstructions.halt 'Number of vehicles must be greater than 0'
    }
    count = n
  }
  
  void wait(double t) {
    if (t < 0) {
      DSLInstructions.halt 'Negative timeout in vehicle selection'
    }
    timeout = t
  }
  
  void id(String s) {
    req.setRequiredId s
  }

  void type(String t) {
    req.setRequiredType t
  }

  void payload(String... payloads) {
    List<PayloadComponent> list = []
    payloads.each  {
      list.add new PayloadComponent() {
            @Override
            String getName() {
              return it;
            }
            @Override
            Map<String,String> getParameters(){
              return Collections.emptyMap();
            }
            @Override
            void setParameter(String key,String value){
              //TODO
            }
          }
    }
    req.setRequiredPayload list
  }

  void near(Position location, double radius) {
    req.setAreaCenter(location)
    req.setAreaRadius(radius)
  }

  @Override
  public NVLVehicleSet build() {
    List<VehicleRequirements> list = []
    (1..count).each {
      list << req
    }
    NVLVehicleSet set = NVLEngine.getInstance().getRuntime().select(list, timeout)
    if (set == NVLVehicleSet.EMPTY) {
      DSLInstructions.halt "Could not select vehicles!"
    }
    set
  }
}