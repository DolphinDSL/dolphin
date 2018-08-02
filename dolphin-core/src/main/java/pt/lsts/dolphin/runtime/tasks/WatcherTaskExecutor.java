package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public final class WatcherTaskExecutor extends TaskExecutor {

	protected WatcherTaskExecutor(WatcherTask task) {
		super(task.getTheTask());
		
	}

	@Override
	protected void onInitialize(Map<Task, List<Node>> allocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected CompletionState onStep() {
		
		return null;
	}

	@Override
	protected void onCompletion() {
		// TODO Auto-generated method stub
		
	}
	
}