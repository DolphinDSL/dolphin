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

    protected MissionPoint(Position pointLocation) {
        this.positionLocation = pointLocation;
    }

    public Position getPositionLocation() {
        return positionLocation;
    }

    public abstract MAVLinkMessage toMavLinkMessage(MAVLinkNode dest, int current);
}
