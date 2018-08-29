package pt.lsts.dolphin.runtime.tasks;

import java.util.List;
import java.util.Map;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeSet;

public class SequentialTask implements Task {

	private final Task first;
	private final Task second;
	public SequentialTask(Task a, Task b) {
		first = a;
		second = b;
	}

	@Override
	public String getId() {
		return first.getId() + " >> " + second.getId();
	}

	@Override
	public boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
		NodeSet set1 = available.clone(), set2 = available.clone();
		if (!first.allocate(set1, allocation) ||
				!second.allocate(set2, allocation)) {
			return false;
		}
		available.clear();
		available.addAll(NodeSet.intersection(set1, set2));
		return true;
	}

	@Override
	public TaskExecutor getExecutor() {
		return new SequentialTaskExecutor(this, first, second);
	}



}
