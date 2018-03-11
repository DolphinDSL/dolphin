package pt.lsts.dolphin.runtime.mavlink;

import java.net.SocketAddress;
import java.util.Collections;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.enums.MAV_MODE;

import pt.lsts.dolphin.runtime.AbstractNode;
import pt.lsts.dolphin.runtime.Payload;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.util.Debuggable;

public final class MAVLinkNode extends AbstractNode implements Debuggable {

  private final SocketAddress sockAddr;
  private Position position = new Position(0, 0, 0);
  private msg_heartbeat lastHB;
  
  protected MAVLinkNode(int sysId, SocketAddress addr) {
    super(String.valueOf(sysId));
    sockAddr = addr;
  }

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

  SocketAddress getAddress() {
    return sockAddr;
  }

  void handleIncomingPacket(MAVLinkPacket packet) {
     switch (packet.msgid) {
       case msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT: {
         msg_global_position_int msg = new msg_global_position_int();
         msg.unpack(packet.payload);
         position = new Position(msg.lat * 1e-07, msg.lon * 1e-07, msg.relative_alt * 1e-03);
         
       }
       break;
         
         
     }
  }

  void onHeartbeat(msg_heartbeat msg) {
    lastHB = msg;
  }
  
  boolean available() {
    return lastHB != null && lastHB.autopilot == MAV_MODE.MAV_MODE_AUTO_ARMED;
  }
}
