package pt.lsts.dolphin.dsl

import pt.lsts.dolphin.runtime.tasks.Task
import pt.lsts.dolphin.runtime.tasks.WatcherTask

class WatcherTaskBuilder extends Builder<WatcherTask>{
	
	Task t
	Closure cl
	
	public WatcherTaskBuilder(Task theTask,Closure e){
		t = theTask
		cl = e
		e.resolveStrategy = Closure.OWNER_FIRST //TODO
	}

	@Override
	public WatcherTask build() {
		return new WatcherTask(t,cl)
	}

}
