package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_set_current;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

import java.util.Collection;
import java.util.Collections;

public class SetCurrentCommand extends DroneCommand {

    private int currentMissionPoint;

    private int repetitions;

    private boolean forceExecute = false;

    private SetCurrentCommand(int currentMissionPoint, int repetitions, boolean forceExecute) {
        this.currentMissionPoint = currentMissionPoint;
        this.repetitions = repetitions;
        this.forceExecute = forceExecute;
    }

    @Override
    public boolean executeOnStartup() {
        return forceExecute;
    }

    public int getTargetMissionPoint() {
        return this.currentMissionPoint;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest) {
        msg_mission_set_current set_current = new msg_mission_set_current();

        set_current.target_system = (short) dest.getMAVLinkId();
        set_current.target_component = 0;
        set_current.seq = currentMissionPoint;

        return Collections.singleton(set_current);
    }

    public static DroneCommand initSetCurrentItem(int currentItem) {
        return initSetCurrentItem(currentItem, 0);
    }

    public static DroneCommand initSetCurrentItem(int currentItem, int repetitions) {
        return initSetCurrentItem(currentItem, repetitions, false);
    }

    public static DroneCommand initSetCurrentItem(int currentItem, int repetitions, boolean forceExecute) {
        return new SetCurrentCommand(currentItem, repetitions, forceExecute);
    }

}
