package pt.lsts.dolphin.runtime.mavlink;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.enums.MAV_AUTOPILOT;
import com.MAVLink.enums.MAV_STATE;
import com.MAVLink.enums.MAV_TYPE;

import pt.lsts.dolphin.runtime.EnvironmentException;
import pt.lsts.dolphin.runtime.MessageHandler;
import pt.lsts.dolphin.util.Clock;
import pt.lsts.dolphin.util.Debug;
import pt.lsts.dolphin.util.Debuggable;

public class MAVLinkCommunications extends Thread implements Debuggable {

  public static void main(String[] args) throws IOException {
    MAVLinkCommunications comm = getInstance();
    Debug.enable(System.out, false);
    comm.start();
  }

  public static MAVLinkCommunications getInstance() { 
    if (INSTANCE == null) {
      INSTANCE = new MAVLinkCommunications();
    }
    return INSTANCE;
  }
  
  private static MAVLinkCommunications INSTANCE = null;
  private static final int GCS_UDP_PORT = 14559;
  private static final int BUFFER_LENGTH = 1024;
  private static final double HEARTBEAT_PERIOD = 1.0;
  private static final int GCS_DOLPHIN_ID = 0xD0;
  private static final byte[] HB_PACKET;

  static {
    // Build GGS heartbeat message
    msg_heartbeat hb = new msg_heartbeat();
    hb.sysid = GCS_DOLPHIN_ID;
    hb.compid = 1;
    hb.type = MAV_TYPE.MAV_TYPE_GCS;
    hb.autopilot = MAV_AUTOPILOT.MAV_AUTOPILOT_INVALID;
    hb.custom_mode = 0;
    hb.system_status = MAV_STATE.MAV_STATE_ACTIVE;
    HB_PACKET = hb.pack().encodePacket();
  }
  
  private boolean active;
  private final HashMap<Integer,MAVLinkNode> nodes = new HashMap<>();
  private final MessageHandler<MAVLinkNode,MAVLinkMessage> mh = new MessageHandler<>();
  private final DatagramSocket udpSocket;
  private final DatagramPacket udpPacket = new DatagramPacket(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH);
  
  private MAVLinkCommunications() {
    super("MAVLink communications");

    try {
      //setDaemon(true);
      udpSocket = new DatagramSocket(GCS_UDP_PORT);
      udpSocket.setSoTimeout(1);
      active = false;
    }
    catch (IOException e) {
      throw new EnvironmentException(e);
    }
    
    mh.bind(msg_heartbeat.class, MAVLinkNode::consume);
    mh.bind(msg_global_position_int.class, MAVLinkNode::consume);
  }
 


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

  @Override
  public void run() {
    active = true;
    while (active) {
      try {
        double timeNow = Clock.now();
        handleHeartbeats(timeNow);
        handleIncomingMessages();
      }
      catch(RuntimeException e) {
        d("Unexpected exception: %s", e.toString());
        e.printStackTrace(System.err);
      }
    }
  }
  private double lastHB = 0;

  private void handleHeartbeats(double timeNow) {
    if (timeNow - lastHB >= HEARTBEAT_PERIOD) {
      lastHB = timeNow;
      for (MAVLinkNode node : nodes.values()) {
        d("HB to MAV %s", node.getId());
        send(HB_PACKET, node);
      }
    }
  }

  private void handleIncomingMessages() {
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
        mh.process(node, message);
      }
    }
  }

  public void send(MAVLinkMessage msg, MAVLinkNode node) {
    send(msg.pack().encodePacket(), node);
  }

  private void send(byte[] data, MAVLinkNode node) {
    try {
      udpSocket.send(new DatagramPacket(data, 0, data.length, node.getAddress()));
    } catch (IOException e) {
      d("Error sending data to node: %s [ %s ]", node.getId(), node.getAddress());
      e.printStackTrace(System.err);
    }
  }
}

