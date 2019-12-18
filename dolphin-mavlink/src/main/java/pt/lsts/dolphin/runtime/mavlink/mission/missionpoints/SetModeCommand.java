package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_command_long;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_MODE_FLAG;
import com.MAVLink.enums.PLANE_MODE;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

public class SetModeCommand extends DroneCommand {

    /**
     * The mode to set
     *
     * @see PLANE_MODE
     */
    private int mode;

    private SetModeCommand(int mode) {
        this.mode = mode;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {

        msg_command_long item = new msg_command_long();

        item.command = MAV_CMD.MAV_CMD_DO_SET_MODE;
        item.target_system = (short) dest.getMAVLinkId();
        item.target_component = 0;

        item.param1 = MAV_MODE_FLAG.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED;
        //Set the drone as auto mode
        item.param2 = mode;

        return item;
    }

    public static DroneCommand initSetMode(int mode) {
        return new SetModeCommand(mode);
    }
}
