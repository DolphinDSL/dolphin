package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item_reached;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import com.MAVLink.enums.PLANE_MODE;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.MissionUploadProtocol;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.*;
import pt.lsts.dolphin.runtime.tasks.CompletionState;
import pt.lsts.dolphin.runtime.tasks.PlatformTaskExecutor;

import java.util.*;

public class MissionExecutor extends PlatformTaskExecutor {

    private Map<SetCurrentCommand, Integer> repetitionsLeft = null;

    private int currentMissionIndex = 0;

    public MissionExecutor(Mission mission) {
        super(mission);

        repetitionsLeft = new HashMap<>();

        for (DroneCommand droneCommand : mission.getCommandList()) {
            if (droneCommand instanceof SetCurrentCommand) {

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
            clearCurrentMission();
        }

        Engine.platform().displayMessage("Starting the drone %d on to the mission %s", getVehicleMAV().getMAVLinkId(), getTask().getId());

        MissionUploadProtocol uploadP = vehicle.getUploadProtocol();
        uploadP.start(mission);

    }

    private void setIntoRTL() {
        DroneCommand droneCommand = SetModeCommand.initSetMode(PLANE_MODE.PLANE_MODE_RTL);

        getVehicleMAV().send(droneCommand.toMavLinkMessage(getVehicleMAV()));
    }

    private void clearCurrentMission() {
        DroneCommand droneCommand = ClearMissionCommand.initClearMissionCommand();

        getVehicleMAV().send(droneCommand.toMavLinkMessage(getVehicleMAV()));
    }

    @Override
    protected CompletionState onStep() {

        MAVLinkNode vehicle = getVehicleMAV();

        msg_heartbeat lastHBReceived = vehicle.getLastHBReceived();

        long custom_mode = lastHBReceived.custom_mode;

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

        this.currentMissionIndex = item_reached.seq;

        int realIndex = getRealIndex(item_reached.seq);

        DroneCommand command = getMission().getCommandList().get(realIndex + 1);

        if (command instanceof SetCurrentCommand) {

            handleJumpCommand((SetCurrentCommand) command);

        } else if (command instanceof ConditionalCommand) {

            handleConditionalCommand((ConditionalCommand) command);

        }

        Engine.platform().displayMessage("Reached the item %d", item_reached.seq);
    }

    private void handleConditionalCommand(ConditionalCommand command) {

        MAVLinkNode vehicleMAV = getVehicleMAV();

        List<DroneCommand> finalCommands = command.testCondition(getVehicleMAV()) ? command.getIfTrue() : command.getIfFalse();

        int currentMissionIndex = this.currentMissionIndex;

        int currentIndex = currentMissionIndex;

        List<MAVLinkMessage> message = new LinkedList<>();

        for (DroneCommand finalCommand : finalCommands) {

            if (finalCommand instanceof MissionPoint) {

                MAVLinkMessage mavLinkMessage = ((MissionPoint) finalCommand).toMavLinkMessage(getVehicleMAV(), currentIndex++);

                message.add(mavLinkMessage);

            } else {

            }
        }


    }

    private void handleJumpCommand(SetCurrentCommand command) {
        int repetitionsLeft = this.repetitionsLeft.get(command);

        if (repetitionsLeft > 0) {
            repetitionsLeft--;

            this.repetitionsLeft.put(command, repetitionsLeft);

            MAVLinkMessage mavLinkMessage = command.toMavLinkMessage(getVehicleMAV());

            getVehicleMAV().send(mavLinkMessage);

            Engine.platform().displayMessage("Drone has been sent to item %d, there are %d repetitions left on this jump command",
                    command.getTargetMissionPoint(), repetitionsLeft);
        }

    }

    /**
     * Handle the current mission
     *
     * @param currentItem
     */
    public void consume(msg_mission_current currentItem) {

//        Engine.platform().displayMessage("The drone has the current mission %d", currentItem.seq);

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
