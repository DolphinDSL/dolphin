package pt.lsts.dolphin.runtime.mavlink;


import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.mission.Mission;
import pt.lsts.dolphin.util.Debuggable;

import java.util.List;

/**
 * Mission upload protocol.
 */
public final class MissionUploadProtocol implements Debuggable {

    /**
     * Logical state.
     */
    public enum State {
        /**
         * Initializing.
         */
        INIT,
        /**
         * In progress.
         */
        IN_PROGRESS,
        /**
         * Completed with success.
         */
        SUCCESS,
        /**
         * Completed with an error.
         */
        ERROR,
    }

    /**
     * Target node.
     */
    private final MAVLinkNode node;

    /**
     * Current logical state.
     */
    private State state;

    /**
     * Current item being processe.
     */
    private int currentItem;

    /**
     * Waypoints to send (temporary support).
     */
    private Position[] waypoints;

    private List<MAVLinkMessage> messageList;

    /**
     * Constructor.
     *
     * @param node Target node.
     */
    MissionUploadProtocol(MAVLinkNode node) {
        this.node = node;
        this.state = State.INIT;
    }

    /**
     * Return number of waypoints (temporary).
     *
     * @return Number of waypoints.
     */
    public int numberOfWaypoints() {
        return waypoints.length;
    }

    /**
     * Get  logical state.
     *
     * @return Current state.
     */
    public State getState() {
        return state;
    }

    /**
     * Start.
     *
     * @param wpts Waypoints.
     */
    public void start(Position[] wpts) {
        d("starting upload protocol");
        this.waypoints = wpts.clone();
        msg_mission_count m = new msg_mission_count();
        m.count = numberOfWaypoints();
        m.target_system = (short) node.getMAVLinkId();
        m.target_component = 0;
        node.send(m);
        currentItem = 0;
        state = State.IN_PROGRESS;
    }

    public void start(Mission mission) {
        d("starting upload protocol");
        Engine.platform().displayMessage("Starting upload protocol");

        List<MAVLinkMessage> mavLinkMessages = mission.toMavLinkMessages(this.node);
        this.messageList = mavLinkMessages;

        Engine.platform().displayMessage("Messages %d", mavLinkMessages.size());

        MAVLinkMessage mavLinkMessage = mavLinkMessages.get(0);

        node.send(mavLinkMessage);

        this.currentItem = 1;
        state = State.IN_PROGRESS;
        Engine.platform().displayMessage("Sent item count");
        d("Sent item count");
    }

    void consume(msg_mission_request msg) {
        d("got consume request");
        Engine.platform().displayMessage("Got consume request");

        Engine.platform().displayMessage(msg.toString());

        //Have to check msg.seq == currentItem - 1 because the item count msg is also stored in the list
        if (state == State.IN_PROGRESS &&
                msg.seq == (currentItem - 1) &&
//                msg.target_system == node.getMAVLinkId() &&
                msg.target_component == 0) {

            MAVLinkMessage mavLinkMessage = messageList.get(currentItem++);

            //This isn't working?

            Engine.platform().displayMessage(mavLinkMessage.toString());
            node.send(mavLinkMessage);

            Engine.platform().displayMessage("Sent mission item");

        } else {
            state = State.ERROR;
        }
    }

    void consume(msg_mission_request_int msg) {

        Engine.platform().displayMessage(msg.toString());

    }

    /**
     * Handler for mission request message.
     *
     * @param msg Incoming message.
     */
    void consumeOld(msg_mission_request msg) {
        d("got request");
        if (state == State.IN_PROGRESS &&
                msg.seq == currentItem &&
                msg.target_system == node.getMAVLinkId() &&
                msg.target_component == 0) {
            Position w = waypoints[currentItem];
            msg_mission_item mi = new msg_mission_item();
            mi.target_system = (short) node.getMAVLinkId();
            mi.target_component = 0;
            mi.frame = MAV_FRAME.MAV_FRAME_GLOBAL_INT;
            mi.seq = currentItem;
            mi.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
            mi.current = 0;
            mi.autocontinue = 1;
            mi.param1 = 0;
            mi.param2 = 50;
            mi.param3 = 0;
            mi.param4 = Float.NaN;
            mi.x = (float) w.lat;
            mi.y = (float) w.lon;
            mi.z = (float) w.hae;
            node.send(mi);
            currentItem++;
        } else {
            state = State.ERROR;
        }
    }

    void consume(msg_mission_ack msg) {

        Engine.platform().displayMessage("Received message ack %s", msg.toString());
        Engine.platform().displayMessage("%d of %d" , currentItem, messageList.size());

        if (state == State.IN_PROGRESS
                && currentItem == (messageList.size() - 1)
                && msg.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED
                && msg.target_system == node.getMAVLinkId()
                && msg.target_component == 0) {

            Engine.platform().displayMessage("Mission sent successfully, sending start mission packet ");

            state = State.SUCCESS;

            Engine.platform().displayMessage("Send the msg for the node to start the mission");

            MAVLinkMessage mavLinkMessage = messageList.get(messageList.size() - 1);

            node.send(mavLinkMessage);

        } else {
            state = State.ERROR;
        }
    }

    void consume(msg_mission_current updateCurrent) {
        d("Drone successfully started mission item %d", updateCurrent.seq);
    }

}
