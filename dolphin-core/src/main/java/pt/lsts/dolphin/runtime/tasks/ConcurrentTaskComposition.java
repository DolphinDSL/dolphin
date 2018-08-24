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
      boolean firstTaskDone = false,
              secondTaskDone = false;
          
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
      	CompletionState state1=firstTaskExec.getCompletionState(), state2=secondTaskExec.getCompletionState();
        if (!state1.finished()) {
          state1 = firstTaskExec.step();
          firstTaskDone = state1.done();
        }
        if (!state2.finished()) {
          state2 = secondTaskExec.step();
          secondTaskDone = state2.done();
        }
        return firstTaskDone && secondTaskDone ?
              new CompletionState(CompletionState.Type.DONE)
            :  state1.error() && state2.error()? reportError(state1,state2) 
            		: new CompletionState(CompletionState.Type.IN_PROGRESS);
      }

	/**
	 * @param state2 
	 * @param state1 
	 * @return
	 */
	public CompletionState reportError(CompletionState state1, CompletionState state2) {
		String errorMsg = state1.data.toString()+"\n"+state2.data.toString();
		return new CompletionState(CompletionState.Type.ERROR,errorMsg);
	}

      @Override
      protected void onCompletion() {

      }
    };
  }



}
