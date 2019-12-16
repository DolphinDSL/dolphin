package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_MODE;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class SetModeCommand extends MissionPoint {

    private SetModeCommand() {
        super(null);
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item item = new msg_mission_item();

        item.command = MAV_CMD.MAV_CMD_DO_SET_MODE;
        item.autocontinue = 1;
        item.target_system = (short) dest.getMAVLinkId();
        item.seq = current;

        item.target_component = 0;

        item.param1 = MAV_MODE.MAV_MODE_AUTO_ARMED;

        return item;
    }

    public static MissionPoint initSetArmedAuto() {
        return new SetModeCommand();
    }
}
