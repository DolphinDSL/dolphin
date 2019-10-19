package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

import java.util.LinkedList;

public class Mission implements Cloneable {

    private LinkedList<MissionPoint> missionPoints;

    private LinkedList<Boolean> completed;

    private boolean started;

    private boolean ended;

    private Mission() {
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

    public void consume(MAVLinkMessage message) {

    }

    public void sendTo(MAVLinkNode node) {

        missionPoints.forEach((missionPoint -> {

            missionPoint.sendTo(node);

        }));

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

    public static Mission initializeMission() {
        return new Mission();
    }
}
