package pt.lsts.dolphin.dsl

import pt.lsts.dolphin.runtime.Environment.FLAG
import pt.lsts.dolphin.runtime.tasks.Task
import pt.lsts.dolphin.runtime.tasks.WatcherTask

class WatcherTaskBuilder extends Builder<WatcherTask>{
	
	Task t
	FLAG flag
	
	public WatcherTaskBuilder(Task theTask,Closure e){
		t = theTask
		flag = FLAG.PROPAGATE //default value
		e.call()
	}
	
	def ignore (){
		flag = FLAG.IGNORE
	}
	
	def propagate () {
		flag = FLAG.PROPAGATE
	}
	

	@Override
	public WatcherTask build() {
		return new WatcherTask(t,flag)
	}

}
