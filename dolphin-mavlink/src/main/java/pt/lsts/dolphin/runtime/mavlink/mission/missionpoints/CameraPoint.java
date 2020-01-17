package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;

import java.util.Collection;
import java.util.Collections;

public class CameraPoint extends MissionPoint {

    private CameraType type;

    private int arg;

    private int timeBetween;

    private CameraPoint(CameraType type, int arg, int timeBetween) {
        super(null);
        this.type = type;
        this.arg = arg;
        this.timeBetween = timeBetween;
    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest, int current) {

        msg_mission_item item = new msg_mission_item();

        item.command = MAV_CMD.MAV_CMD_IMAGE_START_CAPTURE;
        item.seq = current;
        item.target_component = 0;
        item.target_system = (short) dest.getMAVLinkId();
        item.autocontinue = 1;
        item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;

        item.param1 = this.timeBetween;
        item.param2 = type == CameraType.IMAGE_COUNT ? 10 : 0;
        item.param3 = type == CameraType.IMAGE_COUNT ? arg : type.getParam();

        return Collections.singleton(item);
    }

    public enum CameraType {
        ONE_PHOTO(1),
        UNTIL_STOP(0),
        IMAGE_COUNT(-1);

        private int param;

        CameraType(int param) {
            this.param = param;
        }

        public int getParam() {
            return param;
        }
    }

    public static MissionPoint initCameraPoint(CameraType type, int arg, int timeBetween) {
        return new CameraPoint(type, arg, timeBetween);
    }

}
