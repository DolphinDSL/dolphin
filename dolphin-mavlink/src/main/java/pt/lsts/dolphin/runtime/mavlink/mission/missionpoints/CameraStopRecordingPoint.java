package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

public class CameraStopRecordingPoint extends MissionPoint {

    private int streamID;

    private CameraStopRecordingPoint(int stream) {
        super(null);

        this.streamID = stream;
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item stop_recording = new msg_mission_item();

        stop_recording.command = MAV_CMD.MAV_CMD_VIDEO_STOP_CAPTURE;

        stop_recording.target_system = (short) dest.getMAVLinkId();
        stop_recording.target_component = 0;
        stop_recording.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        stop_recording.seq = current;
        stop_recording.autocontinue = 1;

        stop_recording.param1 = this.streamID;

        return stop_recording;
    }

    public static MissionPoint initCameraStopRecording(int stream) {
        return new CameraStopRecordingPoint(stream);
    }

}
