package pt.lsts.dolphin.runtime.mavlink;


import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import com.MAVLink.enums.MAV_MISSION_TYPE;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.mission.Mission;
import pt.lsts.dolphin.util.Debuggable;

import java.util.List;
import java.util.Map;

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
         * Clearing the current drone mission
         */
        CLEARING,
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
    private int currentMissionItem;

    /**
     * Waypoints to send (temporary support).
     */
    private Position[] waypoints;

    private List<MAVLinkMessage> messageList;

    private Map<Integer, List<MAVLinkMessage>> droneCommands;

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
        currentMissionItem = 0;
        state = State.IN_PROGRESS;
    }

    public void start(Mission mission) {
        d("starting upload protocol");
        Engine.platform().displayMessage("Starting upload protocol");

        this.state = State.CLEARING;
        Engine.platform().displayMessage("Clearing mission.");
        msg_mission_clear_all clear_mission = new msg_mission_clear_all();

        clear_mission.target_component = 0;
        clear_mission.target_system = (short) node.getMAVLinkId();

        node.send(clear_mission);

        this.messageList = mission.toMissionMessages(this.node);
        this.droneCommands = mission.droneCommandsToMissionItem(this.node);
    }

    void startMissionUpload() {
        Engine.platform().displayMessage("Messages %d", messageList.size());

        Engine.platform().displayMessage("Starting dispatch of mission to drone %d", node.getMAVLinkId());

        MAVLinkMessage mavLinkMessage = messageList.get(currentMissionItem++);

        node.send(mavLinkMessage);

        state = State.IN_PROGRESS;
    }

    public void addPointsToMission(int startIndex, List<MAVLinkMessage> toAdd) {

        int d_startIndex = startIndex;

        for (MAVLinkMessage mavLinkMessage : toAdd) {
            this.messageList.add(d_startIndex++, mavLinkMessage);
        }

        msg_mission_write_partial_list write_partial_list = new msg_mission_write_partial_list();

        write_partial_list.target_component = 0;
        write_partial_list.target_system = (short) node.getMAVLinkId();

        write_partial_list.mission_type = MAV_MISSION_TYPE.MAV_MISSION_TYPE_MISSION;

        write_partial_list.start_index = (short) startIndex;

        write_partial_list.end_index = (short) this.messageList.size();

        currentMissionItem = startIndex;

    }

    void consume(msg_mission_request msg) {
        d("got consume request");
        Engine.platform().displayMessage("Got consume request");

        Engine.platform().displayMessage(msg.toString());

        //Have to check msg.seq == currentItem - 1 because the item count msg is also stored in the list
        if (state == State.IN_PROGRESS &&
                msg.seq == currentMissionItem &&
                msg.target_component == 0) {

            MAVLinkMessage mavLinkMessage = messageList.get(currentMissionItem++);

            List<MAVLinkMessage> mavLinkMessages = this.droneCommands.get(currentMissionItem);

            if (mavLinkMessage != null) {
                mavLinkMessages.forEach(node::send);
            }

            node.send(mavLinkMessage);

            Engine.platform().displayMessage("Sent mission item %d", currentMissionItem);
        } else {
            state = State.ERROR;
        }
    }

    void consume(msg_mission_ack ack) {
        if (state == State.CLEARING
                && ack.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED) {

            Engine.platform().displayMessage("Cleared the drone's mission, starting upload of the new mission.");

            startMissionUpload();

        } else if (state == State.IN_PROGRESS
                && ack.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED) {

            Engine.platform().displayMessage("Mission sent successfully.");

            state = State.SUCCESS;

            List<MAVLinkMessage> mavLinkMessages = this.droneCommands.get(currentMissionItem);

            if (mavLinkMessages != null) mavLinkMessages.forEach(node::send);

        }
    }

    void consume(msg_mission_request_int msg) {

        Engine.platform().displayMessage(msg.toString());

    }

//    void consume(msg_mission_ack msg) {
//
//        Engine.platform().displayMessage("Received message ack %s", msg.toString());
//        Engine.platform().displayMessage("%d of %d" , currentItem, messageList.size());
//
//        if (state == State.IN_PROGRESS
//                && currentItem == (messageList.size() - 1)
//                && msg.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED
//                && msg.target_system == node.getMAVLinkId()
//                && msg.target_component == 0) {
//
//            Engine.platform().displayMessage("Mission sent successfully, sending start mission packet ");
//
//            state = State.SUCCESS;
//
//            Engine.platform().displayMessage("Send the msg for the node to start the mission");
//
//            MAVLinkMessage mavLinkMessage = messageList.get(messageList.size() - 1);
//
//            node.send(mavLinkMessage);
//
//        } else {
//            state = State.ERROR;
//        }
//    }

    void consume(msg_mission_current updateCurrent) {
        d("Drone successfully started mission item %d", updateCurrent.seq);
    }

}
