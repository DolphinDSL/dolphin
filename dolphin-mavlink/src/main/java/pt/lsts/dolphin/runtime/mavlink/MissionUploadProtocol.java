package pt.lsts.dolphin.runtime.mavlink;


import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import com.MAVLink.enums.MAV_MISSION_RESULT;

import pt.lsts.dolphin.runtime.Position;

final class MissionUploadProtocol {
  public enum State {
    INIT,
    IN_PROGRESS,
    SUCCESS,
    ERROR,
  }

  private final MAVLinkNode node;
  private State state;
  private int currentItem;
  private Position[] waypoints; 

  MissionUploadProtocol(MAVLinkNode node) {
    this.node = node;
    this.state = State.INIT;
  }

  int numberOfWaypoints() {
    return waypoints.length;
  }

  State getState() {
    return state;
  }
  
  void start(Position[] wpts) {
    this.waypoints = wpts.clone();
    msg_mission_count m = new msg_mission_count();
    m.count = numberOfWaypoints();
    m.target_system = (short) node.getMAVLinkId();
    m.target_component = 0;
    currentItem = 0;
    state = State.IN_PROGRESS;
  }

  void consume(msg_mission_request mr) {
    if (state == State.IN_PROGRESS &&
        mr.seq == currentItem && 
        mr.target_system == node.getMAVLinkId() && 
        mr.target_component == 0) {
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

  void consume(msg_mission_ack ack) {
    if (state == State.IN_PROGRESS &&
        currentItem == numberOfWaypoints() && 
        ack.type != MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED &&
        ack.target_system == node.getMAVLinkId() && 
        ack.target_component == 0) {
      state = State.SUCCESS;       
    } 
    else {
      state = State.ERROR;
    }
  }
}
