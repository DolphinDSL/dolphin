package pt.lsts.nvl.dsl

import pt.lsts.nvl.dsl.Operators
import pt.lsts.nvl.runtime.NVLVehicleSet

import static pt.lsts.nvl.runtime.NVLVehicleSet.*

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asList
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

  
  def setupSpec() {
    Operators.main()
  }

  def 'Basic' () {
    when:
       def a = new NVLVehicleSet (v1,v2,v3)
       
    then:
       ! a.empty
       a.size() == 3
       asList(a) == [v1,v2,v3]
  }
  
  def 'Singleton' () {
    when:
       def a = singleton(v1)
       
    then:
       ! a.empty
       a.size() == 1
       asList(a) == [v1]
  }
  
  def 'Operations' () {
    when:
       def a = new NVLVehicleSet (v1, v2, v3, v4)
       def b = new NVLVehicleSet (v3, v4, v5, v6)
    then:
       asList(a+b) == [v1, v2, v3, v4, v5, v6]
       asList(a-b) == [v1,v2]
       asList(a & b) == [v3,v4]
       asList( a | {  v -> v.getType() == 'UUV' }) == [v2,v4]
   }
  
  
  
}
