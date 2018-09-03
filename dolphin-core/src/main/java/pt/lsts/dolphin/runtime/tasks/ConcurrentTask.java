package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public class ConcurrentTask implements Task {

  private final Task first;
  private final Task second;
  public ConcurrentTask(Task a, Task b) {
    first = a;
    second = b;
  }

  @Override
  public String getId() {
    return first.getId() + " | " + second.getId();
  }

  
  @Override
  public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
    return first.allocate(available, allocation) && second.allocate(available, allocation);
  }

  @Override
  public TaskExecutor getExecutor() {
    return new ConcurrentTaskExecutor(this,first,second); 
  }
  
}
