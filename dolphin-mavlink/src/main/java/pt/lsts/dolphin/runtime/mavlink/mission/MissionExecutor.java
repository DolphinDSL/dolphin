package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.MissionUploadProtocol;
import pt.lsts.dolphin.runtime.tasks.CompletionState;
import pt.lsts.dolphin.runtime.tasks.PlatformTaskExecutor;

public class MissionExecutor extends PlatformTaskExecutor {

    private msg_mission_item_reached last_item;

    private boolean[] completed;

    public MissionExecutor(Mission mission) {
        super(mission);

        completed = new boolean[mission.missionPoints()];
    }

    MAVLinkNode getVehicleMAV() {
        return (MAVLinkNode) getVehicle();
    }

    @Override
    protected void onStart() {
        MAVLinkNode vehicle = (MAVLinkNode) getVehicle();

        Mission mission = (Mission) getTask();

        MissionUploadProtocol uploadP = vehicle.getUploadProtocol();
        uploadP.start(mission);
    }

    @Override
    protected CompletionState onStep() {

        MAVLinkNode vehicle = getVehicleMAV();

        msg_heartbeat lastHBReceived = vehicle.getLastHBReceived();

        long custom_mode = lastHBReceived.custom_mode;

        completed[this.last_item.seq] = true;

        if (this.last_item.seq == completed.length) {
            return new CompletionState(CompletionState.Type.DONE);
        }

        if (custom_mode == 11) {
            return new CompletionState(CompletionState.Type.DONE);
        } else if (custom_mode == 10) {
            return new CompletionState(CompletionState.Type.IN_PROGRESS);
        }

        return new CompletionState(CompletionState.Type.ERROR, "");
    }

    public void consume(msg_mission_item_reached item_reached) {
        this.last_item = item_reached;

        Engine.platform().displayMessage("Reached the item %d", item_reached.seq);
    }

    public void consume(msg_mission_ack mission_received) {

        if (mission_received.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED) {
            Engine.platform().displayMessage("The drone has received the mission.");

            msg_mission_set_current start = new msg_mission_set_current();

            start.target_system = (short) getVehicleMAV().getMAVLinkId();
            start.target_component = 0;
            start.seq = 0;

            getVehicleMAV().send(start);

            Engine.platform().displayMessage("Sent the mission start command to the drone.");
        } else {
            Engine.platform().displayMessage("Failed to send the mission to the drone");
            Engine.platform().displayMessage("%d", mission_received.type);
        }

    }

    @Override
    protected void onCompletion() {
        d("The mission " + getTask().getId() + " has been completed by the vehicle " + getVehicleMAV().getMAVLinkId() + ".");
    }
}
