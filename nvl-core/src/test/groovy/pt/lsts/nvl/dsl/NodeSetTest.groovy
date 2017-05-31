package pt.lsts.nvl.dsl

import pt.lsts.nvl.dsl.Operators
import pt.lsts.nvl.runtime.NodeSet

import pt.lsts.nvl.runtime.Node

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asList
import spock.lang.Specification

class NodeSetTest extends Specification {
  def v1 = Mock(Node) {
    getId() >> 'v1'
    getType() >> 'UAV'
  }
  def v2 = Mock(Node) {
    getId() >> 'v2'
    getType() >> 'UUV'
  }
  def v3 = Mock(Node) {
    getId() >> 'v3'
    getType() >> 'USV'
  }
  def v4 = Mock(Node) {
    getId() >> 'v4'
    getType() >> 'UUV'
  }
  def v5 = Mock(Node) {
    getId() >> 'v5'
    getType() >> 'UAV'
  }
  
  def v6 = Mock(Node) {
    getId() >> 'v6'
    getType() >> 'UAV'
  }

  
  def setupSpec() {
    Operators.main()
  }

  def 'Basic' () {
    when:
       def a = new NodeSet (v1,v2,v3)
       
    then:
       ! a.empty
       a.size() == 3
       asList(a) == [v1,v2,v3]
  }
  
  def 'Singleton' () {
    when:
       def a = NodeSet.singleton(v1)
       
    then:
       ! a.empty
       a.size() == 1
       asList(a) == [v1]
  }
  
  def 'Operations' () {
    when:
       def a = new NodeSet (v1, v2, v3, v4)
       def b = new NodeSet (v3, v4, v5, v6)
    then:
       asList(a+b) == [v1, v2, v3, v4, v5, v6]
       asList(a-b) == [v1,v2]
       asList(b-a) == [v5,v6]
       asList(a-a) == []
       asList(a & b) == [v3,v4]
       asList( a | {  v -> v.getType() == 'UUV' }) == [v2,v4]
   }
  
  
  
}
