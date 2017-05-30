package pt.lsts.nvl.runtime;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;


public final class NVLVehicleSet implements Iterable<NVLVehicle> {

  public static final NVLVehicleSet EMPTY = 
      new NVLVehicleSet(Collections.unmodifiableMap(Collections.emptyMap()));
  
  private final Map<String,NVLVehicle> theSet;
  
  public NVLVehicleSet() {
    this(new LinkedHashMap<>());
  }
  
  public NVLVehicleSet(NVLVehicle... vehicles) {
    this(Arrays.asList(vehicles));
  }
  
  public NVLVehicleSet(Iterable<NVLVehicle> vehicles) {
    this(new LinkedHashMap<>());
    for (NVLVehicle v : vehicles) {
      add(v);
    }
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

  public void addAll(Iterable<? extends NVLVehicle> vehicles) {
     for (NVLVehicle v :  vehicles) {
       add(v);
     }
  }

 
  public void remove(NVLVehicle v) {
     theSet.remove(v.getId()); 
  }

  public void removeAll(Iterable<? extends NVLVehicle> vehicles) {
    for (NVLVehicle v : vehicles) {
      remove(v);
    }
 }
  
  public static NVLVehicleSet union(NVLVehicleSet a, NVLVehicleSet b) {
    NVLVehicleSet unionSet = new NVLVehicleSet();
    unionSet.addAll(a);
    unionSet.addAll(b);
    return unionSet;
  }
  
  public static NVLVehicleSet difference(NVLVehicleSet a, NVLVehicleSet b) {
    NVLVehicleSet diffSet = new NVLVehicleSet();
    diffSet.addAll(a);
    diffSet.removeAll(b);
    return diffSet;
  }
  
  public static NVLVehicleSet intersection(NVLVehicleSet a, NVLVehicleSet b) {
    NVLVehicleSet iSet = new NVLVehicleSet();
    for (NVLVehicle v : a) {
      if (b.contains(v)) {
        iSet.add(v);
      }
    }
    for (NVLVehicle v : b) {
      if (a.contains(v)) {
        iSet.add(v);
      }
    }
    return iSet;
  }
  
  public static NVLVehicleSet subset(NVLVehicleSet original, Predicate<NVLVehicle> pred) {
    NVLVehicleSet set = new NVLVehicleSet();
    for (NVLVehicle v : original) {
      if (pred.test(v)) {
        set.add(v);
      }
    }
    return set;
  }
  
  public static NVLVehicleSet singleton(NVLVehicle v) {
    NVLVehicleSet set = new NVLVehicleSet();
    set.add(v);
    return set;
  }
  

  
}
