package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.Mission;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;
import pt.lsts.dolphin.runtime.mavlink.mission.PointPayload;

public class TakeOffPoint extends MissionPoint {


    private float pitch;

    private float yaw;

    private TakeOffPoint(Position position, PointPayload payload, float pitch, float yaw) {
        super(position, payload, PointType.TAKEOFF);

        this.pitch = pitch;

        this.yaw = yaw;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item takeOff = new msg_mission_item();

        takeOff.command = getPointType().getMAV_CMD();
        takeOff.target_component = 0;
        takeOff.target_system = (short) dest.getMAVLinkId();

        takeOff.current = 0;
        takeOff.autocontinue = 1;
        takeOff.frame = MAV_FRAME.MAV_FRAME_GLOBAL_INT;
        takeOff.seq = current;

        takeOff.param1 = pitch;
        takeOff.param4 = yaw;

        takeOff.x = (float) getPositionLocation().lat;
        takeOff.y = (float) getPositionLocation().lon;
        takeOff.z = (float) getPositionLocation().hae;

        return takeOff;
    }

    public static MissionPoint initTakeOffPoint(Position pos, float pitch, float yaw) {
        return new TakeOffPoint(pos, null, pitch, yaw);
    }
}
