package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import groovy.lang.Closure;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public  class WatcherTask implements Task {
	
	  private final Task theTask;
	  private final Object error;
	 
	  public WatcherTask (Task t,Closure onError){
		  error = onError.call();
		  theTask = t;
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
		// TODO Auto-generated method stub
		return false;
	}
	
}