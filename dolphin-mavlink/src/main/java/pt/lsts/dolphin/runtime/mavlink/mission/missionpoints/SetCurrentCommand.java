package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_set_current;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

public class SetCurrentCommand extends DroneCommand {

    private int currentMissionPoint;

    private int repetitions;

    private SetCurrentCommand(int currentMissionPoint, int repetitions) {
        this.currentMissionPoint = currentMissionPoint;
        this.repetitions = repetitions;
    }

    public int getTargetMissionPoint() {
        return this.currentMissionPoint;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {
        msg_mission_set_current set_current = new msg_mission_set_current();

        set_current.target_system = (short) dest.getMAVLinkId();
        set_current.target_component = 0;
        set_current.seq = currentMissionPoint;

        return set_current;
    }

    public static DroneCommand initSetCurrentItem(int currentItem) {
        return initSetCurrentItem(currentItem, 0);
    }

    public static DroneCommand initSetCurrentItem(int currentItem, int repetitions) {
        return new SetCurrentCommand(currentItem, repetitions);
    }

}
