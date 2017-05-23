package pt.lsts.nvl.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;


public final class NVLVehicleSet implements Iterable<NVLVehicle> {

  public static final NVLVehicleSet EMPTY = 
      new NVLVehicleSet(Collections.unmodifiableMap(Collections.emptyMap()));
  
  private final Map<String,NVLVehicle> theSet;
  
  public NVLVehicleSet() {
    this(new LinkedHashMap<>());
  }
  
  private NVLVehicleSet(Map<String,NVLVehicle> set) {
    theSet = set;
  }

  public int size() {
    return theSet.size();
  }
  
  public boolean isEmpty() {
    return theSet.isEmpty();
    
  }
  
  public boolean add(NVLVehicle v) {
    return theSet.putIfAbsent(v.getId(), v) == null;
  }
  
  public boolean contains(NVLVehicle v) {
    return theSet.containsKey(v.getId());
  }

  @Override
  public Iterator<NVLVehicle> iterator() {
    return theSet.values().iterator();
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    theSet.keySet().forEach(id -> sb.append(' ').append(id) );
    return sb.append(' ').append('}').toString();
  }

  public Stream<NVLVehicle> stream() {
    return theSet.values().stream();
  }

  public void addAll(Collection<? extends NVLVehicle> coll) {
     for (NVLVehicle v : coll) {
       add(v);
     }
  }

  public void remove(NVLVehicle v) {
     theSet.remove(v.getId()); 
  }

}
