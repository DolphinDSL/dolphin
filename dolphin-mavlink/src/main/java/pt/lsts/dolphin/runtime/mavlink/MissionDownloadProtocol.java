package pt.lsts.dolphin.runtime.mavlink;


import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.MAVLink.enums.MAV_MISSION_RESULT;

import pt.lsts.dolphin.util.Debuggable;

final class MissionDownloadProtocol implements Debuggable {
  public enum State {
    INIT,
    IN_PROGRESS,
    SUCCESS,
    ERROR,
  }

  private final MAVLinkNode node;
  private State state;
  private int currentItem;
  private int totalItems;
  
  MissionDownloadProtocol(MAVLinkNode node) {
    this.node = node;
    this.state = State.INIT;
  }
  
  State getState() {
    return state;
  }
  
  void start() {
    msg_mission_request_list mrl = new msg_mission_request_list();
    mrl.target_system = (short) node.getMAVLinkId();
    mrl.target_component = 0;
    d("MDP >> %s - request", node.getId());
    node.send(mrl);
    state = State.IN_PROGRESS;
    currentItem = -1;
  }

  void consume(msg_mission_count mc) {
    d("MDP << %s - mission count: %d - %s", node.getId(), mc.count, mc);
    if (state == State.IN_PROGRESS && mc.count >= 0) {
      totalItems = mc.count;
      if (totalItems == 0) {
        sendAck();
      } else {
        sendReq(0);
      }
    } else {
      state = State.ERROR;
    }
  }
  
  void consume(msg_mission_item mi) {
    d("MDP << %s - mi %d - %s", node.getId(), mi.seq, mi);
    if (state == State.IN_PROGRESS &&
        mi.seq == currentItem) {
        if (mi.seq != totalItems-1) {
          sendReq(mi.seq + 1);
        }
        else {
          sendAck();
        }
    }
    else {
      state = State.ERROR;
    }
  }
  
  private void sendReq(int seq) {
    d("MDP >> %s - mr %d", node.getId(), seq);
    msg_mission_request mr = new msg_mission_request();
    mr.target_system = (short) node.getMAVLinkId();
    mr.target_component = 0;
    mr.seq = seq;
    node.send(mr);
    currentItem = seq;
  }
  
  private void sendAck() {
    d("MDP >> %s - ack", node.getId());
    msg_mission_ack ack = new msg_mission_ack();
    ack.target_system = (short) node.getMAVLinkId();
    ack.target_component = 0;
    ack.type = MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED;
    node.send(ack);
    state = State.SUCCESS;
  }

}
