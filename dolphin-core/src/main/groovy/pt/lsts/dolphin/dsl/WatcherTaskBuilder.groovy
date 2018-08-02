package pt.lsts.dolphin.dsl

import pt.lsts.dolphin.runtime.tasks.*

@DSLClass
class WatcherTaskBuilder extends Builder<GuardedTaskSet> {

  
  def onError(Closure<Exception> err) {
	  
  }
  
  @Override
  public WatcherTask build() {
	
	new WatcherTask()
  }
}
