package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

import java.util.Collection;
import java.util.Collections;

public class SetHomeCommand extends MissionPoint {

    private SetHomeCommand(Position home) {
        super(home);
    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item msg = new msg_mission_item();

        msg.target_system = (short) dest.getMAVLinkId();
        msg.target_component = 0;
        msg.autocontinue = 1;
        msg.command = MAV_CMD.MAV_CMD_DO_SET_HOME;
        msg.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        msg.seq = current;

        msg.param1 = 0;

        msg.x = (float) (getPositionLocation().lat * Position.R2D);
        msg.y = (float) (getPositionLocation().lon * Position.R2D);
        msg.z = (float) getPositionLocation().hae;

        return Collections.singleton(msg);
    }

    public static MissionPoint initSetHome(Position pos) {
        return new SetHomeCommand(pos);
    }
}
