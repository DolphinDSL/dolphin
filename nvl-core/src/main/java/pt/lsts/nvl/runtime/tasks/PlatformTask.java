package pt.lsts.nvl.runtime.tasks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.NodeSet;
import pt.lsts.nvl.runtime.NodeFilter;


public abstract class PlatformTask implements Task { 
  
 private final String id;
  
  public PlatformTask(String id) {
    this.id = id;
  }

  @Override
  public final String getId() {
    return id;
  }
  
  public abstract List<NodeFilter> getRequirements();
  
  @Override
  public final boolean allocate(NodeSet available, Map<Task,List<Node>> allocation) {
    LinkedList<Node> selection = new LinkedList<>();
    LinkedList<NodeFilter> requirements = new LinkedList<>();
    //getRequirements(requirements);
    
    d("Requirements: %s", getRequirements());
    d("Vehicles: %s", available);
  
    for (NodeFilter r : requirements) {
      Optional<Node> optV = available.stream().filter(v -> r.matchedBy(v)).findFirst();
      if (optV.isPresent()) {
        Node v = optV.get();
        available.remove(v);
        selection.add(v);
        d("Selected: %s", v.getId());
      } else {
        d("No match for %s", r);
        break;
      }
    }
    boolean success = selection.size() == requirements.size();
    if (success) {
      for (Node n : selection) {
        msg("Task %s allocated node %s.",  getId(), n.getId());
      }
      allocation.put(this, selection); 
    } else {
      msg("Task %s could not allocate necessary nodes.", getId());
      available.addAll(selection); // undo
    }
    return success;
  }
  
  
  
}


