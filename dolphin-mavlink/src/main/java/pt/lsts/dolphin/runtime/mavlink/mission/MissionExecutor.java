package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import com.MAVLink.enums.MAV_MODE_FLAG;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.MissionUploadProtocol;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ArmCommand;
import pt.lsts.dolphin.runtime.tasks.CompletionState;
import pt.lsts.dolphin.runtime.tasks.PlatformTaskExecutor;

public class MissionExecutor extends PlatformTaskExecutor {

    private msg_mission_item_reached last_item;

    private msg_mission_current msg_mission_current;

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

        Engine.platform().displayMessage("Arming drone...");

        ArmCommand armCommand = ArmCommand.initArmCommand(1);

        MAVLinkMessage mavLinkMessage = armCommand.toMavLinkMessage(getVehicleMAV());

        getVehicleMAV().send(mavLinkMessage);

        vehicle.setExecutor(this);

        if (vehicle.getLastHBReceived().custom_mode == 10) {
            //Vehicle is currently in auto mode
            Engine.platform().displayMessage("The drone %d is in auto mode, changing to RTL ", getVehicleMAV().getMAVLinkId());

            setIntoRTL();
        }

        Engine.platform().displayMessage("Starting the drone %d on to the mission %s", getVehicleMAV().getMAVLinkId(), getTask().getId());

        MissionUploadProtocol uploadP = vehicle.getUploadProtocol();
        uploadP.start(mission);

    }

    private void setIntoRTL() {
        msg_command_long set_mode = new msg_command_long();

        set_mode.command = MAV_CMD.MAV_CMD_DO_SET_MODE;

        set_mode.target_component = 0;
        set_mode.target_system = (short) getVehicleMAV().getMAVLinkId();
        set_mode.param1 = MAV_MODE_FLAG.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED;
        //Set the drone as RTL mode
        set_mode.param2 = 11;
    }

    @Override
    protected CompletionState onStep() {

        MAVLinkNode vehicle = getVehicleMAV();

        msg_heartbeat lastHBReceived = vehicle.getLastHBReceived();

        long custom_mode = lastHBReceived.custom_mode;

//        Engine.platform().displayMessage("On step %d", custom_mode);

        if (custom_mode == 11 && timeElapsed() >= 5) {
            Engine.platform().displayMessage("DONE");
            return new CompletionState(CompletionState.Type.DONE);
        }

        return new CompletionState(CompletionState.Type.IN_PROGRESS, "");
    }

    public void consume(msg_mission_item_reached item_reached) {
        this.last_item = item_reached;

        Engine.platform().displayMessage("Reached the item %d", item_reached.seq);
    }

    /**
     * Handle the current mission
     *
     * @param currentItem
     */
    public void consume(msg_mission_current currentItem) {
        this.msg_mission_current = currentItem;

//        Engine.platform().displayMessage("Current Mission item %d", currentItem.seq);
    }

//    public void consume(msg_camera_image_captured camera_captured) {
//
//        Engine.platform().displayMessage("Captured an image %d", camera_captured.image_index);
//
//    }

    public void consume(msg_mission_ack mission_received) {

        if (getVehicleMAV().getUploadProtocol().getState() == MissionUploadProtocol.State.CLEARING) {
            return;
        }

        if (mission_received.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED) {
            Engine.platform().displayMessage("The drone %d has received the mission.", this.getVehicleMAV().getMAVLinkId());

            msg_command_long set_mode = new msg_command_long();

            set_mode.command = MAV_CMD.MAV_CMD_DO_SET_MODE;

            set_mode.target_component = 0;
            set_mode.target_system = (short) getVehicleMAV().getMAVLinkId();
            set_mode.param1 = MAV_MODE_FLAG.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED;
            //Set the drone as auto mode
            set_mode.param2 = 10;

            getVehicleMAV().send(set_mode);

            msg_mission_set_current start = new msg_mission_set_current();

            start.target_system = (short) getVehicleMAV().getMAVLinkId();
            start.target_component = 0;
            start.seq = 1;

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
