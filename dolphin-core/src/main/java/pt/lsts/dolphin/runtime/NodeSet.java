package pt.lsts.dolphin.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;


public final class NodeSet implements Iterable<Node>, Cloneable {

  public static final NodeSet EMPTY = 
      new NodeSet(Collections.unmodifiableMap(Collections.emptyMap()));
  
  private final Map<String,Node> theSet;
  
  public NodeSet() {
    this(new LinkedHashMap<>());
  }
  
  public NodeSet(Node... nodes) {
    this(Arrays.asList(nodes));
  }
  
  public NodeSet(Collection<? extends Node> nodes) {
    this(new LinkedHashMap<>());
    for (Node v : nodes) {
      add(v);
    }
  }
  
  private NodeSet(Map<String,Node> set) {
    theSet = set;
  }

  public int size() {
    return theSet.size();
  }
  
  public boolean isEmpty() {
    return theSet.isEmpty();
    
  }
  
  public boolean add(Node v) {
    return theSet.putIfAbsent(v.getId(), v) == null;
  }
  
  public boolean contains(Node v) {
    return theSet.containsKey(v.getId());
  }

  @Override
  public NodeSet clone() {
    NodeSet copy = new NodeSet();
    copy.addAll(this);
    return copy;
  }
  
  @Override
  public Iterator<Node> iterator() {
    return theSet.values().iterator();
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    theSet.keySet().forEach(id -> sb.append(' ').append(id) );
    return sb.append(' ').append('}').toString();
  }

  public Stream<Node> stream() {
    return theSet.values().stream();
  }

  public void addAll(Iterable<? extends Node> nodes) {
     for (Node v :  nodes) {
       add(v);
     }
  }

 
  public void remove(Node v) {
     theSet.remove(v.getId()); 
  }

  public void removeAll(Iterable<? extends Node> nodes) {
    for (Node v : nodes) {
      remove(v);
    }
 }
  
  public void clear() {
    theSet.clear(); 
  }
  
  public static NodeSet union(NodeSet a, NodeSet b) {
    NodeSet unionSet = new NodeSet();
    unionSet.addAll(a);
    unionSet.addAll(b);
    return unionSet;
  }
  
  public static NodeSet difference(NodeSet a, NodeSet b) {
    NodeSet diffSet = new NodeSet();
    diffSet.addAll(a);
    diffSet.removeAll(b);
    return diffSet;
  }
  
  public static NodeSet intersection(NodeSet a, NodeSet b) {
    NodeSet iSet = new NodeSet();
    for (Node v : a) {
      if (b.contains(v)) {
        iSet.add(v);
      }
    }
    for (Node v : b) {
      if (a.contains(v)) {
        iSet.add(v);
      }
    }
    return iSet;
  }
  
  public static NodeSet subset(NodeSet original, Predicate<Node> pred) {
    NodeSet set = new NodeSet();
    for (Node v : original) {
      if (pred.test(v)) {
        set.add(v);
      }
    }
    return set;
  }
  
  public static NodeSet singleton(Node v) {
    NodeSet set = new NodeSet();
    set.add(v);
    return set;
  }


  

  
}
