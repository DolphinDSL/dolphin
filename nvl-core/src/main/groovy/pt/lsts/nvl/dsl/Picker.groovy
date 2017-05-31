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