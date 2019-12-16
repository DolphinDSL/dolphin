package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class StopCameraCapture extends MissionPoint {

    private StopCameraCapture() {
        super(null);
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item item = new msg_mission_item();

        item.command = MAV_CMD.MAV_CMD_IMAGE_STOP_CAPTURE;
        item.target_system = (short) dest.getMAVLinkId();
        item.target_component = 0;
        item.seq = current;
        item.autocontinue = 1;

        return item;
    }

    public static MissionPoint initStopCapture() {
        return new StopCameraCapture();
    }
}
