package pt.lsts.dolphin.runtime.mavlink;


import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import com.MAVLink.enums.MAV_MISSION_RESULT;

import pt.lsts.dolphin.runtime.Position;

/**
 * Mission upload protocol.
 */
final class MissionUploadProtocol {
  
  /**
   * Logical state.
   */
  public enum State {
    /**
     * Initializing.
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
   * Current item being processe.
   */
  private int currentItem;
  
  /**
   * Waypoints to send (temporary supprort).
   */
  private Position[] waypoints; 

  /**
   * Constructor.
   * @param node Target node.
   */
  MissionUploadProtocol(MAVLinkNode node) {
    this.node = node;
    this.state = State.INIT;
  }

  /**
   * Return number of waypoints (temporary).
   * @return Number of waypoints.
   */
  public int numberOfWaypoints() {
    return waypoints.length;
  }

  /**
   * Get  logical state.
   * @return Current state.
   */
  public State getState() {
    return state;
  }
  
  /**
   * Start.
   * @param wpts Waypoints.
   */
  public void start(Position[] wpts) {
    this.waypoints = wpts.clone();
    msg_mission_count m = new msg_mission_count();
    m.count = numberOfWaypoints();
    m.target_system = (short) node.getMAVLinkId();
    m.target_component = 0;
    node.send(m);
    currentItem = 0;
    state = State.IN_PROGRESS;
  }

  /**
   * Handler for mission request message.
   * @param msg Incoming message.
   */
  void consume(msg_mission_request msg) {
    if (state == State.IN_PROGRESS &&
        msg.seq == currentItem && 
        msg.target_system == node.getMAVLinkId() && 
        msg.target_component == 0) {
      Position w = waypoints[currentItem];
      msg_mission_item mi = new msg_mission_item();
      mi.target_system = (short) node.getMAVLinkId();
      mi.target_component = 0;
      mi.frame = MAV_FRAME.MAV_FRAME_GLOBAL_INT;
      mi.seq = currentItem;
      mi.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
      mi.current = 0;
      mi.autocontinue = 1;
      mi.param1 = 0;
      mi.param2 = 50;
      mi.param3 = 0;
      mi.param4 = Float.NaN;
      mi.x = (float) w.lat;
      mi.y = (float) w.lon;
      mi.z = (float) w.hae;
      node.send(mi);
      currentItem++;
    }
    else {
      state = State.ERROR;
    }
  }

  /**
   * Handler for mission acknowlegement message.
   * @param msg Incoming message.
   */
  void consume(msg_mission_ack msg) {
    if (state == State.IN_PROGRESS &&
        currentItem == numberOfWaypoints() && 
        msg.type != MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED &&
        msg.target_system == node.getMAVLinkId() && 
        msg.target_component == 0) {
      state = State.SUCCESS;       
    } 
    else {
      state = State.ERROR;
    }
  }
}