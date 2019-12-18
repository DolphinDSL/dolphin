package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_set_current;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

public class SetCurrentCommand extends DroneCommand {

    private int currentMissionPoint;

    private SetCurrentCommand(int currentMissionPoint) {
        this.currentMissionPoint = currentMissionPoint;
    }

    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {
        msg_mission_set_current set_current = new msg_mission_set_current();

        set_current.target_system = (short) dest.getMAVLinkId();
        set_current.target_component = 0;
        set_current.seq = currentMissionPoint;

        return set_current;
    }

    public static DroneCommand initSetCurrentItem(int currentItem) {
        return new SetCurrentCommand(currentItem);
    }

}
