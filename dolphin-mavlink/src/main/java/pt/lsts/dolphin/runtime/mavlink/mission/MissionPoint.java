package pt.lsts.dolphin.runtime.mavlink.mission;

import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

public class MissionPoint {

    private Position positionLocation;

    private PointPayload payload;

    private PointType pointType;

    public void sendTo(MAVLinkNode node) {
        //TODO: Send position
        //TODO: send payload

    }

    public enum PointType {

        WAYPOINT,
        LOITER,
        LOITER_DIST,
        LOITER_TIME
        /**
         * TODO: Add more modes
         */

    }

}
