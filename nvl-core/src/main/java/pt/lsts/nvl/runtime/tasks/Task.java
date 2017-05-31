package pt.lsts.nvl.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.nvl.runtime.Node;
import pt.lsts.nvl.runtime.NodeSet;
import pt.lsts.nvl.util.Debuggable;

public interface Task extends Debuggable {
  String getId();
  TaskExecutor getExecutor();
  boolean allocate(NodeSet available, Map<Task,List<Node>> allocation);

}
