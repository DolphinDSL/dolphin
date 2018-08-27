package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.ErrorHandler;
import pt.lsts.dolphin.runtime.Node;

public class WatcherTaskExecutor extends TaskExecutor {
	
	private  TaskExecutor exec;
    private ErrorHandler errHandler;

	protected WatcherTaskExecutor(Task theTask,ErrorHandler errH) {
		super(theTask);
		exec = theTask.getExecutor();
		errHandler = errH;
	}

	@Override
	protected void onInitialize(Map<Task, List<Node>> allocation) {
		exec.initialize(allocation);

	}

	@Override
	protected void onStart() {
		exec.start();

	}

	@Override
	protected CompletionState onStep() {
		CompletionState state = exec.step();
		if(state.error()){
			String msg = exec.getCompletionState().getErrorMesssage();
			return errHandler.handleError(msg);
		}
		return state;
	}

	@Override
	protected void onCompletion() {
		exec.onCompletion();

	}

}
