package pt.lsts.dolphin.runtime.mavlink;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_AUTOPILOT;
import com.MAVLink.enums.MAV_COMPONENT;
import com.MAVLink.enums.MAV_STATE;
import com.MAVLink.enums.MAV_TYPE;

import pt.lsts.dolphin.runtime.EnvironmentException;
import pt.lsts.dolphin.runtime.MessageHandler;
import pt.lsts.dolphin.util.Clock;
import pt.lsts.dolphin.util.Debuggable;

/**
 * MAVLink communications manager.
 *
 * Note: singleton pattern is employed (only one instance) at least for now.
 */
public class MAVLinkCommunications extends Thread implements Debuggable {
  
  /**
   * Instance creation lock.
   */
  private static final Object CREATION_LOCK = new Object();
  
  /**
   * Singleton instance.
   */
  private static MAVLinkCommunications INSTANCE = null;
  
  /**
   * Listening port (fixed for now).
   */
  private static final int GCS_UDP_PORT = 14559;
  
  /**
   * Length of buffer for serialization.
   */
  private static final int BUFFER_LENGTH = 1024;
  
  /**
   * Heartbeat period. 
   */
  private static final double HEARTBEAT_PERIOD = 1.0;
  
  /**
   * Id used by Dolphin as impersonation of a GCS.
   */
  private static final int GCS_DOLPHIN_ID = 0xD0;
  
  /**
   * Get instance.
   * @return Instance of communications manager.
   */
  public static MAVLinkCommunications getInstance() { 
    synchronized(CREATION_LOCK) {
      if (INSTANCE == null) {
        INSTANCE = new MAVLinkCommunications();
      }
      return INSTANCE;
    }
  }

  /**
   * Active flag.
   */
  private boolean active;
  
  /**
   * Map of addresses to nodes.
   */
  private final Map<Integer,MAVLinkNode> nodes = new ConcurrentHashMap<>();
  
  /**
   * Message handler map.
   */
  private final MessageHandler<MAVLinkNode,MAVLinkMessage> msgHandler = new MessageHandler<>();
 
  /** 
   * UDP socket.
   */
  private final DatagramSocket udpSocket;
   
  /**
   * Time of last heartbeat sent.
   */
  private double lastHBSent = 0;

  /**
   * Constructor.
   */
  private MAVLinkCommunications() {
    super("MAVLink communications");
    try {
      setDaemon(true);
      udpSocket = new DatagramSocket(GCS_UDP_PORT);
      udpSocket.setSoTimeout(100);
      active = false;
    }
    catch (IOException e) {
      throw new EnvironmentException(e);
    }

    // Setup message handlers.
    msgHandler.bind(msg_heartbeat.class, MAVLinkNode::consume);
    msgHandler.bind(msg_global_position_int.class, MAVLinkNode::consume);
    msgHandler.bind(msg_mission_ack.class, MAVLinkNode::consume);
    msgHandler.bind(msg_mission_request.class, MAVLinkNode::consume);
    msgHandler.bind(msg_mission_count.class, MAVLinkNode::consume);
    msgHandler.bind(msg_mission_item.class, MAVLinkNode::consume);
    msgHandler.bind(msg_mission_current.class, MAVLinkNode::consume);
  }

  /**
   * Receiving thread execution.
   */
  @Override
  public void run() {
    active = true;
    while (active) {
      try {
        double timeNow = Clock.now();
        handleOutgoingHeartbeats(timeNow);
        handleIncomingMessages();
      }
      catch(RuntimeException e) {
        d("Unexpected exception: %s", e.toString());
        e.printStackTrace(System.err);
      }
    }
  }
  
  /**
   * Terminate communications.
   */
  @SuppressWarnings("deprecation")
  public void terminate() {
    if (active && isAlive()) {
      try {
        join(10);
      } catch(InterruptedException e) {

      }
      if (isAlive()) {
        stop();
      }
    }
    active = false;
  }
  
  /**
   * Send a MAVlink message.
   * @param msg Message.
   * @param node Target node.
   */
  public void send(MAVLinkMessage msg, MAVLinkNode node) {
    d("OUT %s >> MAV %s", msg.getClass().getSimpleName(), node.getId());
    MAVLinkPacket packet = msg.pack();
    packet.sysid = GCS_DOLPHIN_ID;
    packet.compid = MAV_COMPONENT.MAV_COMP_ID_ALL;
    send(packet, node);
  }

  /**
   * Send a MAVlink packet (already in packed form).
   * @param packet Packet.
   * @param node Target node.
   */
  public void send(MAVLinkPacket packet, MAVLinkNode node) {
    try {
      byte[] data = packet.encodePacket();
      udpSocket.send(new DatagramPacket(data, 0, data.length, node.getAddress()));
    } catch (IOException e) {
      d("Error sending data to node: %s [ %s ]", node.getId(), node.getAddress());
      e.printStackTrace(System.err);
    }
  }
  
  /**
   * Get all connected nodes.
   * @return Collection of nodes.
   */
  public Collection<MAVLinkNode> getNodes() {
    return nodes.values();
  }

  /**
   * Get node by system id.
   * @param systemId System id.
   * @return Handle to node, or null if not found.
   */
  public MAVLinkNode getNode(int systemId) {
    return nodes.get(systemId);
  }
  
  /**
   * Send heartbeats periodically.
   * @param timeNow Current time.
   */
  private void handleOutgoingHeartbeats(double timeNow) {
    if (timeNow - lastHBSent >= HEARTBEAT_PERIOD) {
      // Build HB message
      msg_heartbeat hb = new msg_heartbeat();
      hb.type = MAV_TYPE.MAV_TYPE_GCS;
      hb.autopilot = MAV_AUTOPILOT.MAV_AUTOPILOT_INVALID;
      hb.custom_mode = 0;
      hb.system_status = MAV_STATE.MAV_STATE_ACTIVE;
      
      // Send to every vehicle
      for (MAVLinkNode node : nodes.values()) {
        d("HB to MAV %s", node.getId());
        send(hb, node);
      }
      
      lastHBSent = timeNow;
    }
  }

  /**
   * Process incoming messages.
   */
  private void handleIncomingMessages() {
    DatagramPacket udpPacket = new DatagramPacket(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH);
    try {
      udpSocket.receive(udpPacket);
    } 
    catch(SocketTimeoutException e) { 
      return;
    } 
    catch (IOException e) {
      throw new EnvironmentException(e);
    }

    Parser parser = new Parser();
    for (byte b : udpPacket.getData()) {
      MAVLinkPacket packet = parser.mavlink_parse_char(b < 0 ? 256 + b:  b);
      if (packet == null) {
        continue;
      }
      //d("PKT: %d, %d, %d, %d", packet.len, packet.sysid, packet.compid, packet.msgid);
      MAVLinkMessage message = packet.unpack();
      MAVLinkNode node = nodes.get(packet.sysid);
      if (message.msgid == msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT) {
        msg_heartbeat hbMsg = (msg_heartbeat) message;
        d("HB from node %d (%s)", hbMsg.sysid, hbMsg.type);
        if (hbMsg.type == MAV_TYPE.MAV_TYPE_FIXED_WING) {
          if (node == null) {
            node =  new MAVLinkNode(hbMsg.sysid, udpPacket.getSocketAddress());
            nodes.put(hbMsg.sysid, node);
            d("New MAV %d", hbMsg.sysid);
          }
        }
      } 
      if (node != null) {
        msgHandler.process(node, message);
      }
    }
  }
}

