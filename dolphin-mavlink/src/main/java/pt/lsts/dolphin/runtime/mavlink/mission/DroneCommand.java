package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

public abstract class DroneCommand {

    public abstract MAVLinkMessage toMavLinkMessage(MAVLinkNode dest);

}
