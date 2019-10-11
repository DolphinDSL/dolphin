package pt.lsts.dolphin.runtime.mavlink;


import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.MAVLink.enums.MAV_MISSION_RESULT;

import pt.lsts.dolphin.util.Debuggable;

/**
 * Handler for downloading a mission from a vehicle.
 */
final class MissionDownloadProtocol implements Debuggable {
  
  /**
   * Logical state.
   */
  public enum State {
    /**
     * Initialized.
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
   * Current mission item.
   */
  private int currentItem;
  
  /** 
   * Total mission items.
   */
  private int totalItems;
  
  /**
   * Constructor.
   * @param node Target node.
   */
  MissionDownloadProtocol(MAVLinkNode node) {
    this.node = node;
    this.state = State.INIT;
  }
  
  /**
   * Get state.
   * @return current state.
   */
  public State getState() {
    return state;
  }
  
  /**
   * Start the protocol.
   */
  public void start() {
    msg_mission_request_list mrl = new msg_mission_request_list();
    mrl.target_system = (short) node.getMAVLinkId();
    mrl.target_component = 0;
    d("MDP >> %s - request", node.getId());
    node.send(mrl);
    state = State.IN_PROGRESS;
    currentItem = -1;
  }

  /**
   * Handler for mission count message.
   * @param msg Incoming message.
   */
  void consume(msg_mission_count msg) {
    d("MDP << %s - mission count: %d - %s", node.getId(), msg.count, msg);
    if (state == State.IN_PROGRESS && msg.count >= 0) {
      totalItems = msg.count;
      if (totalItems == 0) {
        sendAck();
      } else {
        sendReq(0);
      }
    } else {
      state = State.ERROR;
    }
  }
  

  /**
   * Handler for mission item message.
   * @param msg Incoming message.
   */
  void consume(msg_mission_item msg) {
    d("MDP << %s - mi %d - %s", node.getId(), msg.seq, msg);
    if (state == State.IN_PROGRESS &&
        msg.seq == currentItem) {
        if (msg.seq != totalItems-1) {
          sendReq(msg.seq + 1);
        }
        else {
          sendAck();
        }
    }
    else {
      state = State.ERROR;
    }
  }
  
  /**
   * Send mission request message.
   * @param seq Sequence number.
   */
  private void sendReq(int seq) {
    d("MDP >> %s - mr %d", node.getId(), seq);
    msg_mission_request mr = new msg_mission_request();
    mr.target_system = (short) node.getMAVLinkId();
    mr.target_component = 0;
    mr.seq = seq;
    node.send(mr);
    currentItem = seq;
  }
  
  /**
   * Send mission acknowledgement message.
   */
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
