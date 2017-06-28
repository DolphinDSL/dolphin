
package pt.lsts.nvl.runtime;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public final class  PayloadComponent {
  
  private final String name;
  private final Map<String,String> parameters;
  
  
  public PayloadComponent(String name) {
    this.name = name;
    this.parameters = Collections.emptyMap(); // immutable
  }
  
  public PayloadComponent(String name, Map<String,String> parameters) {
    this.name = name;
    this.parameters = Collections.unmodifiableMap(parameters);
  }
  
  public String getName() {
    return name;
  }
  
  public Map<String,String> getParameters() {
    return parameters;
  }
  
  @Override 
  public boolean equals(Object o) {
    return o == this || 
           (      o instanceof PayloadComponent 
               && getName().equals(((PayloadComponent) o).getName())
           );
//        		   Arrays.equals(toArray(), ((PayloadComponent) o).toArray()));
  }
  
  @Override 
  public int hashCode() {
    return Arrays.hashCode(toArray());
  }
  
  private Object[] toArray() {
    return new Object[] { name, parameters };
  }
  
  @Override
  public String toString(){
	  return getName();
  }
}
