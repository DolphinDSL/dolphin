package pt.lsts.nvl.dsl;

import pt.lsts.nvl.runtime.*

@DSLClass
final class Picker extends Builder<NodeSet> {

  private NodeFilter req = new NodeFilter()
  private int count = 1
  private double timeout = 0
 
  void count(int n) {
    if (n <= 0) {
      Instructions.halt 'Number of vehicles must be greater than 0'
    }
    count = n
  }
  
  void timeout(double t) {
    if (t < 0) {
      Instructions.halt 'Negative timeout in vehicle selection'
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
    for (String p : payloads) {
      list.add new PayloadComponent(p) 
    }
    req.setRequiredPayload new Payload(list)
  }

  void region(Area a) {
    req.setRegion(a)
  }
  
  void region(Position location, double radius) {
    req.setRegion new Area(location, radius)
  }

  @Override
  public NodeSet build() {
    List<NodeFilter> list = []
    (1..count).each {
      list << req
    }
    NodeSet set = Engine.runtime().select(list, timeout)
    if (set == NodeSet.EMPTY) {
      Engine.halt "Could not find required vehicles!"
    }
    set
  }
}