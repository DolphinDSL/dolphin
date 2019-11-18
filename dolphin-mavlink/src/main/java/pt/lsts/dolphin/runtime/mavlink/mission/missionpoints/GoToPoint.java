package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;
import pt.lsts.dolphin.runtime.mavlink.mission.PointPayload;

public class GoToPoint extends MissionPoint {

    private GoToPoint(Position pointLocation, PointPayload payload) {
        super(pointLocation, payload);
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode node, int current) {

        msg_mission_item msg_item = new msg_mission_item();

        msg_item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;

        msg_item.target_system = (short) node.getMAVLinkId();
        msg_item.target_component = 0;
        msg_item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        msg_item.seq = current;
        msg_item.current = 0;
        msg_item.autocontinue = 1;
        msg_item.param1 = 0;
        msg_item.param2 = 50;
        msg_item.param3 = 0;
        msg_item.param4 = Float.NaN;

        msg_item.x = (float) (getPositionLocation().lat * Position.R2D);
        msg_item.y = (float) (getPositionLocation().lon * Position.R2D);
        msg_item.z = (float) getPositionLocation().hae;

        return msg_item;
    }

    /**
     * Create a simple goto mission point for the drone to follow
     *
     * @param latInDeg  The latitude of the point in degrees
     * @param longInDeg The longitude of the point in degrees
     * @param haeInDeg  The height of the position
     * @return The initialized MissionPoint
     */
    public static MissionPoint initGoToPoint(double latInDeg, double longInDeg, double haeInDeg) {
        return initGoToPoint(Position.fromDegrees(latInDeg, longInDeg, haeInDeg));
    }

    public static MissionPoint initGoToPoint(Position position) {
        return new GoToPoint(position, null);
    }
}
