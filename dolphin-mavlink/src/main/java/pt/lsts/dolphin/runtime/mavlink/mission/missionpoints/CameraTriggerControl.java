package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class CameraTriggerControl extends MissionPoint {

    private int enable;

    private int reset;

    private int pause;

    private CameraTriggerControl(int enable, int reset, int pause) {
        super(null);

        this.enable = enable;
        this.reset = reset;
        this.pause = pause;

    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item trigger = new msg_mission_item();

        trigger.command = MAV_CMD.MAV_CMD_DO_TRIGGER_CONTROL;

        trigger.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        trigger.target_component = 0;
        trigger.target_system = (short) dest.getMAVLinkId();
        trigger.autocontinue = 1;
        trigger.seq = current;

        trigger.param1 = enable;
        trigger.param2 = reset;
        trigger.param3 = pause;

        return trigger;
    }

    public static MissionPoint initTriggerControl(int enable, int reset, int pause) {
        return new CameraTriggerControl(enable, reset, pause);
    }
}
