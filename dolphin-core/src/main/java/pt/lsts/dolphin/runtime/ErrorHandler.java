package pt.lsts.dolphin.runtime;

import pt.lsts.dolphin.runtime.tasks.CompletionState;

public interface ErrorHandler {

	
	 CompletionState handleError(String message);
}
