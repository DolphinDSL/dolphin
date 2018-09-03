package pt.lsts.dolphin.dsl

import groovy.lang.Closure
import pt.lsts.dolphin.runtime.ErrorHandler
import pt.lsts.dolphin.runtime.tasks.CompletionState
import pt.lsts.dolphin.runtime.tasks.Task
import pt.lsts.dolphin.runtime.tasks.WatcherTask

class ErrorHandlerCode extends Builder<WatcherTask> implements ErrorHandler{
	
	Task t
	Closure<ErrorHandler> errorCode = null
	CompletionState csToReturn 
	
	public ErrorHandlerCode(Task theTask,Closure cl) {
		t  = theTask
		def code = cl.rehydrate(this, cl.getOwner(), cl.getThisObject())
		code.resolveStrategy = Closure.DELEGATE_FIRST //using Builder code to prevent the prematurely call of the closure
		errorCode = code
		propagate() //Default behavior
	}
	
	void  ignore(){
		csToReturn = new CompletionState(CompletionState.Type.DONE)
		Engine.msg("Ignoring runtime error")
	}
	
	void propagate(){
		csToReturn = new CompletionState(CompletionState.Type.ERROR)
	}

	@Override
	public CompletionState handleError(String message) {
		if(errorCode!=null) {
			errorCode.call(message)
		}
		csToReturn = new CompletionState(csToReturn.type,message)
		csToReturn
	}
	
	@Override
	public WatcherTask build() {
		return new WatcherTask(t,this)
	}

}
