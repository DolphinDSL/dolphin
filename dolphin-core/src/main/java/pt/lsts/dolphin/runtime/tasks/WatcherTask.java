package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import groovy.lang.Closure;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public  class WatcherTask implements Task {
	
	  private final Task theTask;

	  private final Closure<Exception> code;

	  public WatcherTask (Task t,Closure<Exception> cl){
		  theTask = t;
		  code = cl;
	  }
	  public Closure<Exception> getClosure() {
		  return this.code;
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
	    return theTask.getExecutor();
	  }

	@Override
	public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
		return theTask.allocate(available, allocation);
	}
	
}