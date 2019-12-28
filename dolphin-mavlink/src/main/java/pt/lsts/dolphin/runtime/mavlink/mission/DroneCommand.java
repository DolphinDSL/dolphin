package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

/**
 * A class for an abstract command to be sent to the drone
 *
 * This command does not have to be part of the mission protocol
 */
public abstract class DroneCommand {

    public abstract MAVLinkMessage toMavLinkMessage(MAVLinkNode dest);

}
