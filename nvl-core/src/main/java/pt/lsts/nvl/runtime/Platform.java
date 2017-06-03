package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.util.Debuggable;

public interface Platform extends Debuggable {
  
  NodeSet getConnectedNodes();
  
  PlatformTask getPlatformTask(String id);
  
  void displayMessage(String format, Object... args);
}
