package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_count;
import pt.lsts.dolphin.runtime.NodeFilter;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.tasks.PlatformTask;
import pt.lsts.dolphin.runtime.tasks.TaskExecutor;

import java.util.*;

public class Mission extends PlatformTask implements Cloneable {

    private LinkedList<MissionPoint> missionPoints;

    private LinkedList<Boolean> completed;

    private boolean started;

    private boolean ended;

    private Mission(String id) {
        super(id);
        missionPoints = new LinkedList<>();
        completed = new LinkedList<>();

        this.started = false;
        this.ended = false;
    }

    public Mission addPoint(MissionPoint missionPoint) {

        this.missionPoints.addLast(missionPoint);
        this.completed.addLast(false);

        return this;
    }

    public void completePoint(int point) {
        this.completed.set(point - 1, true);

        boolean allCompleted = true;

        for (Boolean pointCompleted : this.completed) {

            allCompleted &= pointCompleted;

        }

        this.ended = allCompleted;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean hasEnded() {
        return this.ended;
    }

    public List<MAVLinkMessage> toMavLinkMessages(MAVLinkNode dest) {

        List<MAVLinkMessage> messages = new ArrayList<>();

        msg_mission_count count = new msg_mission_count();

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
        return Collections.emptyList();
    }

    @Override
    public TaskExecutor getExecutor() {
        return new MissionExecutor(this);
    }

}
