package pt.lsts.dolphin.runtime.mavlink;

import java.net.SocketAddress;
import java.util.Collections;

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
import pt.lsts.dolphin.util.Debuggable;

public final class MAVLinkNode extends AbstractNode implements Debuggable {

  private final int mavId;
  private final SocketAddress sockAddr;
  private Position position = new Position(0, 0, 0);
  private msg_heartbeat lastHB;
  private final MissionUploadProtocol mup; 
  private final MissionDownloadProtocol mdp; 
  
  protected MAVLinkNode(int sysId, SocketAddress addr) {
    super(String.valueOf(sysId));
    mavId = sysId;
    sockAddr = addr;
    mup = new MissionUploadProtocol(this);
    mdp = new MissionDownloadProtocol(this);
  }
  

  public int getMAVLinkId() {
    return mavId;
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


  void consume(msg_global_position_int msg) {
    position = Position.fromDegrees(msg.lat * 1e-07, msg.lon * 1e-07, msg.relative_alt * 1e-03);
    // d("%s - Position update: %s", getId(), position);
  }
 
  void consume(msg_heartbeat msg) {
    lastHB = msg;
  }
  
  void consume(msg_mission_ack ack) {
    mup.consume(ack);
  }
  
  void consume(msg_mission_request mr) {
    mup.consume(mr);
  }
  
  void consume(msg_mission_item mi) {
    mdp.consume(mi);
  }
  
  void consume(msg_mission_count mc) {
    mdp.consume(mc);
  }
  
  void send(MAVLinkMessage message) {
    MAVLinkCommunications.getInstance().send(message, this);
  }

  boolean available() {
    return lastHB != null && lastHB.autopilot == MAV_MODE.MAV_MODE_AUTO_ARMED;
  }


  MissionDownloadProtocol getDownloadProtocol() {
    return mdp;
  }

}
