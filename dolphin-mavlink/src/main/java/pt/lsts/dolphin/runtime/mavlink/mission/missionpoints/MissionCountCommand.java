package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_count;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

public class MissionCountCommand extends DroneCommand {

    private int messageCount = 0;

    private MissionCountCommand(int messageCount) {
        this.messageCount = messageCount;
    }

    @Override
    public boolean executeOnStartup() {
        return true;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {

        msg_mission_count msg = new msg_mission_count();

        msg.target_component = 0;
        msg.target_system = (short) dest.getMAVLinkId();

        msg.count = messageCount;

        return msg;
    }

    public static DroneCommand initMissionCountCommand(int messageCount) {
        return new MissionCountCommand(messageCount);
    }
}
