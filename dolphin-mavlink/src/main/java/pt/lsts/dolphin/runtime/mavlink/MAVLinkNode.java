package pt.lsts.dolphin.runtime.mavlink;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.LinkedList;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.enums.MAV_MODE;

import pt.lsts.dolphin.runtime.AbstractNode;
import pt.lsts.dolphin.runtime.Payload;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.mission.Mission;
import pt.lsts.dolphin.util.Debuggable;

/**
 * MAVLink node.
 * <p>
 * It represents a vehicle in the network.
 */
public final class MAVLinkNode extends AbstractNode implements Debuggable {

    /**
     * System id for the node.
     */
    private final int sysId;

    /**
     * Socket address.
     */
    private final SocketAddress sockAddr;

    /**
     * Last known position.
     */
    private Position position = new Position(0, 0, 0);

    /**
     * Last HB received.
     */
    private msg_heartbeat lastHBReceived;

    /**
     * The current mission the node is executing
     */
    private Mission currentMission;

    /**
     * The previous missions done by this node
     * <p>
     * This is stored as a FILO design so to get the latest mission, you get the first node
     */
    private LinkedList<Mission> previousMissions, nextMissions;

    /**
     * Handle for mission upload protocol.
     */
    private final MissionUploadProtocol mup;

    /**
     * Handle for mission download protocol.
     */
    private final MissionDownloadProtocol mdp;

    /**
     * Constructor.
     *
     * @param sysId System id.
     * @param addr  Socket address.
     */
    public MAVLinkNode(int sysId, SocketAddress addr) {
        super(String.valueOf(sysId));
        this.sysId = sysId;
        sockAddr = addr;
        mup = new MissionUploadProtocol(this);
        mdp = new MissionDownloadProtocol(this);

        previousMissions = new LinkedList<>();
    }


    /**
     * Get System id.drone
     *
     * @return the system id.
     */
    public int getMAVLinkId() {
        return sysId;
    }

    /**
     * Get type of vehicle (fixed to "UAV" for now).
     */
    @Override
    public String getType() {
        return "UAV";
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Payload getPayload() {
        return new Payload(Collections.emptyList());
    }

    @Override
    public void release() {

    }

    /**
     * Get the socket address of the communication endpoint.
     *
     * @return A socket address.
     */
    public SocketAddress getAddress() {
        return sockAddr;
    }

    /**
     * Send a message to the node.
     *
     * @param message Message to send.
     */
    public void send(MAVLinkMessage message) {
        MAVLinkCommunications.getInstance().send(message, this);
    }

    /**
     * Check if node is available (communications are active and autopilot is on).
     *
     * @return true if node is available.
     */
    public boolean available() {
        return lastHBReceived != null && lastHBReceived.autopilot == MAV_MODE.MAV_MODE_AUTO_ARMED;
    }


    /**
     * Get handle for mission download protocol.
     *
     * @return The handle for the mission download protocol.
     */
    public MissionDownloadProtocol getDownloadProtocol() {
        return mdp;
    }

    /**
     * Get handle for mission upload protocol.
     *
     * @return The handle for the mission download protocol.
     */
    public MissionUploadProtocol getUploadProtocol() {
        return mup;
    }

    /**
     * Attempt to start a given mission
     *
     * @param mission The mission for the node to execute
     * @return If the mission has been successfully started
     */
    public boolean startMission(Mission mission) {

        if (this.currentMission != null && !this.currentMission.hasEnded()) {
            //Drone is currently in the middle of a mission
            //TODO
            return false;
        } else if (this.currentMission != null) {

            //Drone's last mission has been completed, and drone is ready to receive mission
            this.previousMissions.addFirst(mission);
        }

        //Receive and commence execution the new mission
        this.currentMission = mission.clone();

        this.currentMission.sendTo(this);

        return true;
    }

    /**Attempt to enqueue a new mission for this drone to execute, after current mission and all enqueued missions
     *
     * @param mission The mission to enqueue
     * @return
     */
    public boolean enqueueMission(Mission mission) {

        this.nextMissions.addLast(mission.clone());

        return true;
    }

    /**
     * Handler for position message.
     *
     * @param msg Incoming message.
     */
    void consume(msg_global_position_int msg) {
        position = Position.fromDegrees(msg.lat * 1e-07, msg.lon * 1e-07, msg.relative_alt * 1e-03);
        // d("%s - Position update: %s", getId(), position);
    }

    /**
     * Handler for heartbeat message.
     *
     * @param msg Incoming message.
     */
    void consume(msg_heartbeat msg) {
        lastHBReceived = msg;
    }

    /**
     * Handler for mission acknowledgement message.
     *
     * @param msg Incoming message.
     */
    void consume(msg_mission_ack msg) {
        mup.consume(msg);
    }

    /**
     * Handler for mission request message.
     *
     * @param msg Incoming message.
     */
    void consume(msg_mission_request msg) {
        mup.consume(msg);
    }

    /**
     * Handler for mission item message.
     *
     * @param msg Incoming messsage.
     */
    void consume(msg_mission_item msg) {
        mdp.consume(msg);
    }

    /**
     * Handler for mission count message.
     *
     * @param msg Incoming messsage.
     */
    void consume(msg_mission_count msg) {
        mdp.consume(msg);
    }

}
