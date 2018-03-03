/**
 * 
 */
package pt.lsts.dolphin.dsl

/**
 * Signal set.
 */
@DSLClass
final class SignalSet {

  private Map<String,Set<Object>> signals = [:]
  
  void post(String id, Object value) {
    Set s = signals[id]
    if (s == null) {
      signals[id] = s = new HashSet<>()
    }
    s << value
  }
  
  void post(Map<String,Object> signals) {
    signals.each  { 
      id, obj -> post id, obj
    }
  }
  
  boolean test(String id, Object value) {
    //println 'test' + id + ' ' + signals[id] + ' ' + value
    Set s = signals[id]
    return s != null && s.contains (value)
  }
  
  boolean test(Map<String,Object> map) {
    for (def e in map) {
      if (! test(e.key, e.value) ) {
        return false
      }
    }
    return true
  }
  
  boolean consume(String id, Object value) {
    Set s = signals[id]
    boolean r = s != null
    if (r) {
      r = s.remove value
      if (r && s.empty) {
        signals.remove id 
      }
    }
    return r
  }
  
  boolean consume(Map<String,Object> map) {
    boolean r = test map
    if (r) {
      for (def e in map) {
        Set curr = signals[e.key]
        //println "rem " + e.key + ' ' + curr + ' ' + e.value 
        curr.remove e.value
        if (curr.empty) { 
          //println "clear"
          signals.remove e.key
        }
      }
    }
    //println toString()
    return r
  }
  
  void clear() {
    signals.clear()
  }
  
  boolean poll(String id) {
    //println 'poll ' + id + ' ' + signals[id]
    return signals[id] != null
  }
  
  boolean poll(Map<String,Closure> map) {
    for (def e in map) {
      if (signals[e.key]?.find(e.value) == null) {
        return false;
      }
    }
    //println 'poll ' + id + ' ' + signals[id]
    return true
  }
  
  int size() {
    int n = 0
    for (def e in signals) {
      n += e.value.size()
    }
    return n
  }
  
  @Override
  String toString() {
    return signals.toString()
  }

}
