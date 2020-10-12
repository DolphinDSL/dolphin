package pt.lsts.dolphin.runtime.tasks;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeFilter;
import pt.lsts.dolphin.runtime.NodeSet;
import pt.lsts.dolphin.runtime.Position;


public abstract class PlatformTask implements Task {

    private final String id;

    public PlatformTask(String id) {
        this.id = id;
    }

    @Override
    public final String getId() {
        return id;
    }

    public abstract List<NodeFilter> getRequirements();

    public Optional<Position> getReferencePosition() {
        return Optional.empty();
    }

    @Override
    public final boolean allocate(NodeSet available, Map<Task, List<Node>> allocation) {
        List<Node> selection = new LinkedList<>();
        List<NodeFilter> requirements = getRequirements();

        d("Requirements: %s", requirements);
        d("Vehicles: %s", available);
        Optional<Position> refPos = getReferencePosition();
        for (NodeFilter r : requirements) {
            Iterator<Node> itr = available.stream().filter(v -> r.matchedBy(v)).iterator();
            if (!itr.hasNext()) {
                d("No match for %s", r);
                break;
            }
            Node pick = itr.next();
            d("Considering: %s", pick.getId());
            if (refPos.isPresent()) {
                Position pos = refPos.get();
                double dBest = pick.getPosition().distanceTo(pos);
                while (itr.hasNext()) {
                    Node n = itr.next();
                    double d = n.getPosition().distanceTo(pos);
                    d("Considering also: %s (%f < %f ?)", n.getId(), d, dBest);
                    if (d < dBest) {
                        pick = n;
                        dBest = d;
                        d("Choice now is %s", pick.getId());
                    }
                }
            }
            available.remove(pick);
            selection.add(pick);
            d("Selected: %s", pick.getId());
        }
        boolean success = selection.size() == requirements.size();
        if (success) {
            for (Node n : selection) {
                msg("Task %s allocated node %s.", getId(), n.getId());
            }
            allocation.put(this, selection);
        } else {
            msg("Task %s could not allocate necessary nodes.", getId());
            available.addAll(selection); // undo
        }
        return success;
    }


}


