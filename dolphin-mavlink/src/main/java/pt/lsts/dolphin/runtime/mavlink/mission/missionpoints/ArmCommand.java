package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_command_long;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class ArmCommand extends MissionPoint {

    private int arm_disarm;

    private ArmCommand(int arm_disarm) {
        super(null, null, PointType.ARM_COMMAND);

        this.arm_disarm = arm_disarm;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_command_long arm_cmd = new msg_command_long();

        arm_cmd.command = getPointType().getMAV_CMD();

        arm_cmd.param1 = arm_disarm;
        arm_cmd.param2 = 0;

        return arm_cmd;
    }

    public static MissionPoint initArmCommand(int arm_disarm) {
        return new ArmCommand(arm_disarm);
    }
}
