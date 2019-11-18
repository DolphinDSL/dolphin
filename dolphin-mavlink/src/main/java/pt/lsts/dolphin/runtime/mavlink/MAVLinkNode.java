package pt.lsts.dolphin.runtime.mavlink;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.LinkedList;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_MODE;

import pt.lsts.dolphin.runtime.AbstractNode;
import pt.lsts.dolphin.runtime.Payload;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.mission.Mission;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionExecutor;
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
     * Handle for mission upload protocol.
     */
    private final MissionUploadProtocol mup;

    /**
     * Handle for mission download protocol.
     */
    private final MissionDownloadProtocol mdp;

    private MissionExecutor currentExecutor;

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

    public msg_heartbeat getLastHBReceived() {
        return lastHBReceived;
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
     * Sets the current mission executor of this drone
     * @param executor
     */
    public void setExecutor(MissionExecutor executor) {
        this.currentExecutor = executor;
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
//        mup.consume(msg);

        if (this.currentExecutor != null) this.currentExecutor.consume(msg);
    }

    /**
     * Handler for mission request message.
     *
     * @param msg Incoming message.
     */
    void consume(msg_mission_request msg) {
        mup.consume(msg);
    }

    void consume(msg_mission_request_int msg) {
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

    void consume(msg_mission_current msg) {
        mup.consume(msg);

        if (this.currentExecutor != null) this.currentExecutor.consume(msg);
    }

    void consume(msg_mission_item_reached msg) {
        if (this.currentExecutor != null) this.currentExecutor.consume(msg);
    }

}
