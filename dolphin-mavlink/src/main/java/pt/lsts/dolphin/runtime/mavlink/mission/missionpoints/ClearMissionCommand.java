package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_clear_all;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

import java.util.Collection;
import java.util.Collections;

public class ClearMissionCommand extends DroneCommand {

    private ClearMissionCommand() {}

    @Override
    public boolean executeOnStartup() {
        return false;
    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest) {

        msg_mission_clear_all msg = new msg_mission_clear_all();

        msg.target_component = 0;
        msg.target_system = (short) dest.getMAVLinkId();
//        msg.mission_type = MAV_MISSION_TYPE.MAV_MISSION_TYPE_ALL;

        return Collections.singleton(msg);
    }

    public static ClearMissionCommand initClearMissionCommand() {
        return new ClearMissionCommand();
    }
}
