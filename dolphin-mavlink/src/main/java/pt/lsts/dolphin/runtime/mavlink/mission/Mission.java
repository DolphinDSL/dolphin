package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_set_current;
import com.MAVLink.enums.MAV_CMD;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.Node;
import pt.lsts.dolphin.runtime.NodeFilter;
import pt.lsts.dolphin.runtime.NodeSet;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.tasks.PlatformTask;
import pt.lsts.dolphin.runtime.tasks.Task;
import pt.lsts.dolphin.runtime.tasks.TaskExecutor;

import java.util.*;

public class Mission extends PlatformTask implements Cloneable {

    private LinkedList<MissionPoint> missionPoints;

    private Mission(String id) {
        super(id);
        missionPoints = new LinkedList<>();
    }

    public void setPoints(LinkedList<MissionPoint> points) {
        this.missionPoints = points;
    }

    public int missionPoints() {
        return this.missionPoints.size();
    }

    public List<MAVLinkMessage> toMavLinkMessages(MAVLinkNode dest) {

        List<MAVLinkMessage> messages = new ArrayList<>();

        msg_mission_count count = new msg_mission_count();

        Engine.platform().displayMessage("Item count: %d", this.missionPoints.size());

        Engine.platform().displayMessage("%s", this.missionPoints);

        count.count = this.missionPoints.size();
        count.target_component = 0;
        count.target_system = (short) dest.getMAVLinkId();

        messages.add(count);

        int current = 0;

        for (MissionPoint missionPoint : this.missionPoints) {
            messages.add(missionPoint.toMavLinkMessage(dest, current++));
        }

        return messages;
    }

    @Override
    public Mission clone() {
        try {
            return (Mission) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static final Random random = new Random();

    public static Mission initializeMission() {
        return initializeMission(String.valueOf(random.nextLong()));
    }

    public static Mission initializeMission(String id) {
        return new Mission(id);
    }

    @Override
    public List<NodeFilter> getRequirements() {
        List<NodeFilter> filters = new LinkedList<>();
        NodeFilter filter = new NodeFilter();

        filters.add(filter);
        return filters;
    }

    @Override
    public TaskExecutor getExecutor() {
        return new MissionExecutor(this);
    }

}
