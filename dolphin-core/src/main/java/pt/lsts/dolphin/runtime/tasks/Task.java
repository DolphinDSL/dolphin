package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Environment;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;
import pt.lsts.dolphin.util.Debuggable;

public interface Task extends Debuggable {
  String getId();
  TaskExecutor getExecutor();
  boolean allocate(NodeSet available, Map<Task,List<Node>> allocation);

  default void msg(String format, Object ... args) {
    Environment.getInstance()
               .getPlatform()
               .displayMessage(format, args);
  }
}
