package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class LandingPoint extends MissionPoint {

    private LandingPoint(Position position) {
        super(position);
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item msg_item = new msg_mission_item();

        msg_item.command = MAV_CMD.MAV_CMD_NAV_LAND;

        msg_item.target_system = (short) dest.getMAVLinkId();
        msg_item.target_component = 0;
        msg_item.seq = current;
        msg_item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        msg_item.current = 0;
        msg_item.autocontinue = 1;
        msg_item.param1 = 0;
        msg_item.param2 = 0;
        msg_item.param3 = 0;

        msg_item.x = (float) (getPositionLocation().lat * Position.R2D);
        msg_item.y = (float) (getPositionLocation().lon * Position.R2D);
        msg_item.z = (float) getPositionLocation().hae;

        return msg_item;
    }

    /**
     * Initialize the loiter point from given coordinates
     *
     * @param lat The latitude
     * @param lon The longitude
     * @param hae The height
     * @return
     */
    public static MissionPoint initLandingPoint(double lat, double lon, double hae) {
        return initLandingPoint(Position.fromDegrees(lat, lon, hae));
    }

    public static MissionPoint initLandingPoint(Position position) {
        return new LandingPoint(position);
    }
}
