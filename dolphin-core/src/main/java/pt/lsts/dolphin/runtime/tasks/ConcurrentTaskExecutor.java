package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;

public class ConcurrentTaskExecutor extends TaskExecutor {
    
    private final TaskExecutor firstTaskExec;
    private final TaskExecutor secondTaskExec;
    CompletionState firstTaskCS, secondTaskCS;
    
    protected ConcurrentTaskExecutor(Task theTask, Task first, Task second) {
        super(theTask);
        firstTaskExec = first.getExecutor();    
        secondTaskExec = second.getExecutor();
        }

    @Override
    protected void onInitialize(Map<Task,List<Node>> allocation) {
        firstTaskExec.initialize(allocation);    
        secondTaskExec.initialize(allocation);
        firstTaskCS = firstTaskExec.getCompletionState();
        secondTaskCS = secondTaskExec.getCompletionState();
    }

    @Override
    protected void onStart() {
        firstTaskExec.start(); 
        secondTaskExec.start();
    }

    @Override
    protected CompletionState onStep() {
        if (!firstTaskCS.finished()) {
            firstTaskCS = firstTaskExec.step();
        }
        if (!secondTaskCS.finished()) {
            secondTaskCS = secondTaskExec.step();
        }

        if (firstTaskCS.done() && secondTaskCS.done()){
             return new CompletionState(CompletionState.Type.DONE);
        }
        if (firstTaskCS.error()) {
            if(!secondTaskCS.finished())
                secondTaskExec.onCompletion();
            return firstTaskCS;
        } 
        if (secondTaskCS.error()) {
            if(!firstTaskCS.finished())
                firstTaskExec.onCompletion();
            return secondTaskCS;
        } 
        return  new CompletionState(CompletionState.Type.IN_PROGRESS);
    }

    @Override
    protected void onCompletion() {
    }
}
