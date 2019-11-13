package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item_reached;
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
    }

    @Override
    protected void onCompletion() {
        d("The mission " + getTask().getId() + " has been completed by the vehicle " + getVehicleMAV().getMAVLinkId() + ".");
    }
}
