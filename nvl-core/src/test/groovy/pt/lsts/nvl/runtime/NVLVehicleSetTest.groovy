package pt.lsts.nvl.runtime

import static pt.lsts.nvl.runtime.NVLVehicleSet.*
import spock.lang.Specification

class NVLVehicleSetTest extends Specification {


  def v1 = Mock(NVLVehicle) {
    getId() >> 'v1'
    getType() >> 'UAV'
  }
  def v2 = Mock(NVLVehicle) {
    getId() >> 'v2'
    getType() >> 'UUV'
  }
  def v3 = Mock(NVLVehicle) {
    getId() >> 'v3'
    getType() >> 'USV'
  }
  def v4 = Mock(NVLVehicle) {
    getId() >> 'v4'
    getType() >> 'UUV'
  }
  def v5 = Mock(NVLVehicle) {
    getId() >> 'v5'
    getType() >> 'UAV'
  }
  
  def v6 = Mock(NVLVehicle) {
    getId() >> 'v6'
    getType() >> 'UAV'
  }

  
  def 'Basic' () {
    when:
       def a = new NVLVehicleSet (v1,v2,v3)
       
    then:
       ! a.empty
       a.size() == 3
       a.asList() == [v1,v2,v3]
  }
  
  def 'Operations' () {
    when:
       def a = new NVLVehicleSet (v1, v2, v3, v4)
       def b = new NVLVehicleSet (v3, v4, v5, v6)
    then:
       union(a, b).asList() == [v1, v2, v3, v4, v5, v6]
       difference(a,b).asList() == [v1,v2]
       intersection(a,b).asList() == [v3,v4]
       subset(a, { v -> v.getType() == 'UUV' }).asList() == [v2,v4]
  }
  
  
  
}
