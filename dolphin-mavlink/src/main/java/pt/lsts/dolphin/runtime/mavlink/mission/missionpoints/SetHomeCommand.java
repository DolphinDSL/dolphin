package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class SetHomeCommand extends MissionPoint {

    private SetHomeCommand(Position home) {
        super(home, null);
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item msg = new msg_mission_item();

        msg.target_system = (short) dest.getMAVLinkId();
        msg.target_component = 0;
        msg.autocontinue = 1;
        msg.command = MAV_CMD.MAV_CMD_DO_SET_HOME;

        msg.param1 = 0;

        msg.x = (float) (getPositionLocation().lat);
        msg.y = (float) getPositionLocation().lon;
        msg.z = (float) getPositionLocation().hae;

        return msg;
    }

    public static MissionPoint initSetHome(Position pos) {
        return new SetHomeCommand(pos);
    }
}
