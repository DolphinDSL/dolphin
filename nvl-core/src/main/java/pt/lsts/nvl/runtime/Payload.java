
package pt.lsts.nvl.runtime;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public final class  Payload implements Iterable<PayloadComponent> {
  
  private final List<PayloadComponent> components;
  
  public Payload(List<PayloadComponent> comp) {
    List<PayloadComponent> list = new LinkedList<>();
    list.addAll(comp);
    components = Collections.unmodifiableList(list);
  }
  
  public boolean compatibleWith(Payload p) {
    return p.components.containsAll(components);
  }
  
  public List<PayloadComponent> getComponents(){
	  return components;
  }
  
  @Override 
  public boolean equals(Object o) {
    return o == this || 
          (o instanceof Payload && components.equals(((Payload) o).components));
  }
  
  @Override 
  public int hashCode() {
    return components.hashCode();
  }

  @Override
  public Iterator<PayloadComponent> iterator() {
    return components.iterator();
  }
}
