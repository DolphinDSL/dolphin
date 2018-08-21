package pt.lsts.dolphin.dsl

import pt.lsts.dolphin.runtime.ErrorMode
import pt.lsts.dolphin.runtime.tasks.Task
import pt.lsts.dolphin.runtime.tasks.WatcherTask

class WatcherTaskBuilder extends Builder<WatcherTask>{
	
	Task t
	Closure cl
	
	public WatcherTaskBuilder(Task theTask,Closure c) {
		t  = theTask
		cl = c
	}
	
	void  ignore(){
		Engine.runtime().getErrorMode().setMode(ErrorMode.Type.IGNORE)
	}
	
	void propagate(){
		Engine.runtime().getErrorMode().setMode(ErrorMode.Type.PROPAGATE)
	}
	@Override
	public WatcherTask build() {
		def code = cl.rehydrate(this, cl.getOwner(), cl.getThisObject())
		code.resolveStrategy = Closure.DELEGATE_FIRST //using Builder code to prevent the prematurely call of the closure
		Engine.runtime().getErrorMode().setClosure(code)
		return new WatcherTask(t)
	}

}
