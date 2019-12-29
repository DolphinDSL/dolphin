package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.*;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import com.MAVLink.enums.MAV_MODE_FLAG;
import com.MAVLink.enums.PLANE_MODE;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.MissionUploadProtocol;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ArmCommand;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetCurrentCommand;
import pt.lsts.dolphin.runtime.tasks.CompletionState;
import pt.lsts.dolphin.runtime.tasks.PlatformTaskExecutor;

import java.util.HashMap;
import java.util.Map;

public class MissionExecutor extends PlatformTaskExecutor {

    private Map<SetCurrentCommand, Integer> repetitionsLeft = null;

    public MissionExecutor(Mission mission) {
        super(mission);

        for (DroneCommand droneCommand : mission.getCommandList()) {
            if (droneCommand instanceof SetCurrentCommand) {

                if (repetitionsLeft == null) {
                    repetitionsLeft = new HashMap<>();
                }

                SetCurrentCommand set_mission_current = (SetCurrentCommand) droneCommand;

                repetitionsLeft.put(set_mission_current, set_mission_current.getRepetitions());

            }
        }

    }

    private Mission getMission() {
        return (Mission) getTask();
    }

    private int getRealIndex(int current) {
        int index = 0, realIndex = 0;

        for (DroneCommand droneCommand : getMission().getCommandList()) {
            realIndex++;

            if (droneCommand instanceof MissionPoint) {
                index++;
            }

            if (index == current) {
                break;
            }
        }

        return realIndex;
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

        if (vehicle.getLastHBReceived().custom_mode == PLANE_MODE.PLANE_MODE_AUTO) {
            //Vehicle is currently in auto mode
            Engine.platform().displayMessage("The drone %d is in auto mode, changing to RTL to receive the new mission.", getVehicleMAV().getMAVLinkId());

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
        set_mode.param2 = PLANE_MODE.PLANE_MODE_RTL;

        getVehicleMAV().send(set_mode);
    }

    @Override
    protected CompletionState onStep() {

        MAVLinkNode vehicle = getVehicleMAV();

        msg_heartbeat lastHBReceived = vehicle.getLastHBReceived();

        long custom_mode = lastHBReceived.custom_mode;

//        Engine.platform().displayMessage("On step %d", custom_mode);

        if (custom_mode == PLANE_MODE.PLANE_MODE_RTL && timeElapsed() >= 5) {

            Engine.platform().displayMessage("Finished the mission %s on the drone %d", this.getTask().getId(), this.getVehicleMAV().getMAVLinkId());

            return new CompletionState(CompletionState.Type.DONE);
        }

        return new CompletionState(CompletionState.Type.IN_PROGRESS, "");
    }

    /**
     * Consume the packet that notifies that the drone has reached a given position
     * @param item_reached The position reached
     */
    public void consume(msg_mission_item_reached item_reached) {

        int realIndex = getRealIndex(item_reached.seq);

        DroneCommand command = getMission().getCommandList().get(realIndex);

        if (command instanceof SetCurrentCommand) {

            int repetitionsLeft = this.repetitionsLeft.get(command);

            if (repetitionsLeft > 0) {
                repetitionsLeft--;

                this.repetitionsLeft.put((SetCurrentCommand) command, repetitionsLeft);

                MAVLinkMessage mavLinkMessage = command.toMavLinkMessage(getVehicleMAV());

                getVehicleMAV().send(mavLinkMessage);

                Engine.platform().displayMessage("Drone has been sent to item %d, there are %d repetitions left on this jump command", ((SetCurrentCommand) command).getTargetMissionPoint(), repetitionsLeft);
            }

        }

        Engine.platform().displayMessage("Reached the item %d", item_reached.seq);
    }

    /**
     * Handle the current mission
     *
     * @param currentItem
     */
    public void consume(msg_mission_current currentItem) {
    }

    public void consume(msg_mission_ack mission_received) {

        if (getVehicleMAV().getUploadProtocol().getState() == MissionUploadProtocol.State.CLEARING) {
            return;
        }

        if (mission_received.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED) {
            Engine.platform().displayMessage("The drone %d has received the mission.", this.getVehicleMAV().getMAVLinkId());
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
