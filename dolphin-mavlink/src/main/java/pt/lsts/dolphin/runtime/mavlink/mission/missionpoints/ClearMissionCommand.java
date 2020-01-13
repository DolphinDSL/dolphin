package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.enums.MAV_MISSION_TYPE;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

public class ClearMissionCommand extends DroneCommand {

    private ClearMissionCommand() {}

    @Override
    public boolean executeOnStartup() {
        return false;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {

        msg_mission_clear_all msg = new msg_mission_clear_all();

        msg.target_component = 0;
        msg.target_system = (short) dest.getMAVLinkId();
//        msg.mission_type = MAV_MISSION_TYPE.MAV_MISSION_TYPE_ALL;

        return msg;
    }

    public static ClearMissionCommand initClearMissionCommand() {
        return new ClearMissionCommand();
    }
}
