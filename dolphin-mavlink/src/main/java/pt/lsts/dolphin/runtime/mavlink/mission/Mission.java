package pt.lsts.dolphin.runtime.mavlink.mission;

import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

import java.util.LinkedList;
import java.util.List;

public class Mission {

    private List<MissionPoint> missionPoints;

    public Mission() {
        missionPoints = new LinkedList<>();
    }

    public Mission addPoint(MissionPoint missionPoint) {

        this.missionPoints.add(missionPoint);

        return this;
    }

    public void sendTo(MAVLinkNode node) {

        missionPoints.forEach((missionPoint -> {

            missionPoint.sendTo(node);

        }));

    }

}
