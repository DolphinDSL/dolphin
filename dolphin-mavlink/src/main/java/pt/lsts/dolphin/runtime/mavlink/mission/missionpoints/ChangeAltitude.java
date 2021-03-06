package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

import java.util.Collection;
import java.util.Collections;

public class ChangeAltitude extends MissionPoint {

    private double altitude;

    private ChangeAltitude(double altitude) {
        super(null);

        this.altitude = altitude;
    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item item = new msg_mission_item();

        item.target_system = (short) dest.getMAVLinkId();
        item.target_component = 0;
        item.autocontinue = 1;
        item.seq = current;
        item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        item.command = MAV_CMD.MAV_CMD_DO_CHANGE_ALTITUDE;

        item.param1 = (float) altitude;
        item.param2 = MAV_FRAME.MAV_FRAME_GLOBAL;

        return Collections.singleton(item);
    }

    public static MissionPoint initChangeAltitude(double newAltitude) {
        return new ChangeAltitude(newAltitude);
    }

}
