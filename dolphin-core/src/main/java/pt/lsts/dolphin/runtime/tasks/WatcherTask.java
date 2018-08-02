package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Environment.FLAG;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public  class WatcherTask implements Task {
	
	  private final Task theTask;

	  private final FLAG flag;

	  public WatcherTask (Task t,FLAG f){
		  flag = f;
		  theTask = t;
	  }
	  public FLAG getFlag() {
		  return this.flag;
	  }
	  
	  /**
	   * @return the theTask
	   */
	  public Task getTheTask() {
		  return theTask;
	  }

	  
	  @Override
	  public String getId() {
	    return theTask.getId();
	  }

	  @Override
	  public TaskExecutor getExecutor() {
	    return new WatcherTaskExecutor(this);
	  }

	@Override
	public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
		return theTask.allocate(available, allocation);
	}
	
}