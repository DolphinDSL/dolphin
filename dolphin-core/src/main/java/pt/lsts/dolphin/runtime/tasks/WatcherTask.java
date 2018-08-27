package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.ErrorHandler;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public  class WatcherTask implements Task {
	
	  private final Task theTask;
	  private final ErrorHandler errHandler;


	  public WatcherTask (Task t,ErrorHandler eH){
		  theTask = t;
		  errHandler  = eH;
	  }
	  
	  /**
	 * @return the errHandler
	 */
	public ErrorHandler getErrHandler() {
		return errHandler;
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
	    return new WatcherTaskExecutor(theTask,errHandler);
	  }

	@Override
	public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
		return theTask.allocate(available, allocation);
	}
	
}