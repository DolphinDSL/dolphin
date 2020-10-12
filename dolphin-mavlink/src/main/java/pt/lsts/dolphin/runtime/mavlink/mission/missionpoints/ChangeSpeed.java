package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

import java.util.Collection;
import java.util.Collections;

public class ChangeSpeed extends MissionPoint {

    private double speed;

    private int speedType;

    private ChangeSpeed(double speed, int speedType) {
        super(null);

        this.speed = speed;

        this.speedType = speedType;
    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest, int current) {
        msg_mission_item item = new msg_mission_item();

        item.target_system = (short) dest.getMAVLinkId();

        item.target_component = 0;
        item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        item.autocontinue = 1;
        item.seq = current;

        item.command = MAV_CMD.MAV_CMD_DO_CHANGE_SPEED;
        item.param1 = speedType;
        item.param2 = (float) speed;
        item.param3 = 10;

        return Collections.singleton(item);
    }

    public static MissionPoint initChangeSpeed(double speed, int speedType) {
        return new ChangeSpeed(speed, speedType);
    }
}
