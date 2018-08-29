package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public final class SequentialTaskExecutor extends TaskExecutor {
	private final TaskExecutor secondTaskExec;
	private final TaskExecutor firstTaskExec;
	boolean firstTaskDone = false;

	public SequentialTaskExecutor(Task task, Task first, Task second) {
		super(task);
		this.firstTaskExec  = first.getExecutor();
    this.secondTaskExec = second.getExecutor();
	}

	@Override
	  protected void onInitialize(Map<Task,List<Node>> allocation) {
	    firstTaskExec.initialize(allocation);    
	    secondTaskExec.initialize(allocation);
	  }

	@Override
	  protected void onStart() {
	    firstTaskExec.start();        
	  }

	@Override
	  protected CompletionState onStep() {
	    CompletionState cs;
	    if (!firstTaskDone) {
	      cs = firstTaskExec.step();
	      if (cs.done()) {
	        firstTaskDone = true;
	        secondTaskExec.start();
	        cs = secondTaskExec.step();
	      }
	      else if(cs.error()) {
	          secondTaskExec.onCompletion();
	          return cs;
	      }
	    } else {
	      cs = secondTaskExec.step();
	    }
	    return cs;
	  }

	@Override
	  protected void onCompletion() {
	  }
}