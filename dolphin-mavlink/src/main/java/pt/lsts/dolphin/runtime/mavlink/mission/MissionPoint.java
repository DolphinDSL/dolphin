package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

import java.util.ArrayList;
import java.util.List;

public abstract class MissionPoint {

    private Position positionLocation;

    private PointPayload payload;

    private PointType pointType;

    protected MissionPoint(Position pointLocation, PointPayload payload, PointType pointType) {
        this.positionLocation = pointLocation;
        this.payload = payload;
        this.pointType = pointType;
    }

    public MissionPoint withPointType(PointType pointType) {
        this.pointType = pointType;
        return this;
    }

    public MissionPoint withPayload(PointPayload pointPayload) {
        this.payload = pointPayload;

        return this;
    }

    public Position getPositionLocation() {
        return positionLocation;
    }

    public PointPayload getPayload() {
        return payload;
    }

    public PointType getPointType() {
        return pointType;
    }

    public abstract MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current);

    public enum PointType {

        WAYPOINT(MAV_CMD.MAV_CMD_NAV_WAYPOINT),
        LOITER(MAV_CMD.MAV_CMD_NAV_LOITER_UNLIM),
        LOITER_DIST(MAV_CMD.MAV_CMD_NAV_LOITER_TURNS),
        LOITER_TIME(MAV_CMD.MAV_CMD_NAV_LOITER_TIME),
        LANDING(MAV_CMD.MAV_CMD_NAV_LAND),
        TAKEOFF(MAV_CMD.MAV_CMD_NAV_TAKEOFF),
        ARM_COMMAND(MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM),
        DISARM_COMMAND(MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM);
        /**
         * TODO: Add more modes
         */

        private int MAV_CMD_;

        PointType(int MAV_LINK_NUMBER) {
            this.MAV_CMD_ = MAV_LINK_NUMBER;
        }

        public int getMAV_CMD() {
            return MAV_CMD_;
        }
    }

}
