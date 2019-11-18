package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

import java.util.ArrayList;
import java.util.List;

public abstract class MissionPoint {

    private Position positionLocation;

    private PointPayload payload;

    protected MissionPoint(Position pointLocation, PointPayload payload) {
        this.positionLocation = pointLocation;
        this.payload = payload;
    }

    public MissionPoint withPayload(PointPayload pointPayload) {
        this.payload = pointPayload;

        return this;
    }

    public Position getPositionLocation() {
        return positionLocation;
    }

    public PointPayload getPayload() {
        return payload;
    }

    public abstract MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current);
}
