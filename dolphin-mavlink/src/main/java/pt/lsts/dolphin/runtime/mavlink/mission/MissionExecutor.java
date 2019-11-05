package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.common.msg_heartbeat;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.MissionUploadProtocol;
import pt.lsts.dolphin.runtime.tasks.CompletionState;
import pt.lsts.dolphin.runtime.tasks.PlatformTaskExecutor;

public class MissionExecutor extends PlatformTaskExecutor {

    public MissionExecutor(Mission mission) {
        super(mission);
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

        if (custom_mode == 11) {
            return new CompletionState(CompletionState.Type.DONE);
        } else if (custom_mode == 10) {
            return new CompletionState(CompletionState.Type.IN_PROGRESS);
        }

        return new CompletionState(CompletionState.Type.ERROR, "");
    }

    @Override
    protected void onCompletion() {
        d("The mission " + getTask().getId() + " has been completed by the vehicle " + getVehicleMAV().getMAVLinkId() + ".");
    }
}
