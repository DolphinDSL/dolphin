package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public class ConcurrentTaskComposition implements Task {

  private final Task first;
  private final Task second;
  public ConcurrentTaskComposition(Task a, Task b) {
    first = a;
    second = b;
  }

  @Override
  public String getId() {
    return first.getId() + " | " + second.getId();
  }

  
  @Override
  public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
    return first.allocate(available, allocation) && second.allocate(available, allocation);
  }

  @Override
  public TaskExecutor getExecutor() {
    final TaskExecutor firstTaskExec = first.getExecutor();
    final TaskExecutor secondTaskExec = second.getExecutor();
    return new TaskExecutor(this) {
      boolean firstTaskCompleted = false,
              secondTaskCompleted = false;
          
      @Override
      protected void onInitialize(Map<Task,List<Node>> allocation) {
        firstTaskExec.initialize(allocation);    
        secondTaskExec.initialize(allocation);
      }

      @Override
      protected void onStart() {
        firstTaskExec.start(); 
        secondTaskExec.start();        
      }

      @Override
      protected CompletionState onStep() {
    	CompletionState state1 = firstTaskExec.getCompletionState(),state2 = secondTaskExec.getCompletionState();
        if (!firstTaskCompleted) {
        	state1 = firstTaskExec.step();
        	firstTaskCompleted = state1.done();
        }
        if (!secondTaskCompleted) {
        	state2 = secondTaskExec.step();
        	secondTaskCompleted = state2.done();
        }
        return firstTaskCompleted && secondTaskCompleted ?
              new CompletionState(CompletionState.Type.DONE)
            :  calculateState(state1,state2);
      }

	/**
	 * @param s2 
	 * @param s1 
	 * @return
	 */
	public CompletionState calculateState(CompletionState s1, CompletionState s2) {
		if(s1.error() && s2.error())
			return new CompletionState(CompletionState.Type.ERROR);
		if((s1.inProgress() || s2.inProgress()))
			return new CompletionState(CompletionState.Type.IN_PROGRESS);
		return new CompletionState(CompletionState.Type.UNDEFINED);
	}

      @Override
      protected void onCompletion() {

      }
    };
  }



}
