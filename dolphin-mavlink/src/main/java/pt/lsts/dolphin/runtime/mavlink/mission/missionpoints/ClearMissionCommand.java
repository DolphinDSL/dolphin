package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_clear_all;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

public class ClearMissionCommand extends DroneCommand {

    private ClearMissionCommand() {}

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {

        msg_mission_clear_all msg = new msg_mission_clear_all();

        msg.target_component = 0;
        msg.target_system = (short) dest.getMAVLinkId();

        return msg;
    }
}
