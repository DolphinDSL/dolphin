package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

import java.util.Collection;
import java.util.Collections;

public class CameraRecordPoint extends MissionPoint {

    private int stream = 0;

    private CameraRecordPoint(int stream) {
        super(null);

        this.stream = stream;
    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item item = new msg_mission_item();

        item.command = MAV_CMD.MAV_CMD_VIDEO_START_CAPTURE;
        item.seq = current;
        item.target_component = 0;
        item.target_system = (short) dest.getMAVLinkId();
        item.autocontinue = 1;
        item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;

        item.param1 = 0;
        item.param2 = 1;

        return Collections.singleton(item);
    }

    public static MissionPoint initCameraRecord(int streamId) {
        return new CameraRecordPoint(streamId);
    }
}
