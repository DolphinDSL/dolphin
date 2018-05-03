package pt.lsts.dolphin.runtime.mavlink;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.enums.MAV_AUTOPILOT;
import com.MAVLink.enums.MAV_STATE;
import com.MAVLink.enums.MAV_TYPE;

import pt.lsts.dolphin.runtime.EnvironmentException;
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

  private final DatagramSocket udpSocket;
  private final DatagramPacket udpPacket 
  = new DatagramPacket(new byte[BUFFER_LENGTH], 0, BUFFER_LENGTH);
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
      double timeNow = Clock.now();
      handleHeartbeats(timeNow);
      handleIncomingMessages();
    }
  }
  private double lastHB = 0;

  private void handleHeartbeats(double timeNow) {
    if (timeNow - lastHB >= HEARTBEAT_PERIOD) {
      lastHB = timeNow;
      for (MAVLinkNode node : nodes.values()) {
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
      d("Unexpected IO/expection: %s", e.getMessage());
      return;
    }
    Parser parser = new Parser();
    for (byte b : udpPacket.getData()) {
      MAVLinkPacket packet = parser.mavlink_parse_char(b < 0 ? 256 + b:  b);
      if (packet == null) {
        continue;
      }
      // d("MSG: %d, %d, %d, %d", packet.len, packet.sysid, packet.compid, packet.msgid);
      MAVLinkNode node = nodes.get(packet.sysid);
      if (packet.msgid == msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT) {
        msg_heartbeat msg = new msg_heartbeat();
        try {
          msg.unpack(packet.payload);
          d("HB from MAV %d", packet.sysid);
          if (msg.type == MAV_TYPE.MAV_TYPE_FIXED_WING) {
            if (node == null) {
              node =  new MAVLinkNode(packet.sysid, udpPacket.getSocketAddress());
              nodes.put(packet.sysid, node);
              d("New MAV %d", packet.sysid);
            }
            
            node.onHeartbeat(msg);
          }
        }
        catch (RuntimeException e) {
          d("Error decoding heartbeat message");
          e.printStackTrace(System.err);
        }
      } else if (node != null) {
        node.handleIncomingPacket(packet);
      }
    }
  }

  private void send(MAVLinkMessage msg, MAVLinkNode node) {
    send(msg.pack().encodePacket(), node);
  }

  private void send(byte[] data, MAVLinkNode node) {
    try {
      d("HB to MAV %s", node.getId());
      udpSocket.send(new DatagramPacket(data, 0, data.length, node.getAddress()));
    } catch (IOException e) {
      d("Error sending data to node: %s [ %s ]", node.getId(), node.getAddress());
      e.printStackTrace(System.err);
    }
  }
}

