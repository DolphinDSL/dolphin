package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_command_long;
import com.MAVLink.enums.MAV_CMD;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class ArmCommand extends DroneCommand {

    private int arm_disarm;

    private ArmCommand(int arm_disarm) {

        this.arm_disarm = arm_disarm;
    }

    @Override
    public boolean executeOnStartup() {
        return true;
    }

    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {

        msg_command_long arm_cmd = new msg_command_long();

        arm_cmd.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
        arm_cmd.target_system = (short) dest.getMAVLinkId();
        arm_cmd.target_component = 0;

        arm_cmd.param1 = arm_disarm;
        arm_cmd.param2 = 0;

        return arm_cmd;
    }

    public static ArmCommand initArmCommand(int arm_disarm) {
        return new ArmCommand(arm_disarm);
    }
}
