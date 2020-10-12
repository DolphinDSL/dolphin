package pt.lsts.dolphin.runtime.mavlink.mission;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.enums.PLANE_MODE;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.NodeFilter;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.MissionCountCommand;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetCurrentCommand;
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetModeCommand;
import pt.lsts.dolphin.runtime.tasks.PlatformTask;
import pt.lsts.dolphin.runtime.tasks.TaskExecutor;

import java.util.*;

public class Mission extends PlatformTask implements Cloneable {

    private LinkedList<DroneCommand> droneCommands;

    private Mission(String id) {
        super(id);
        this.droneCommands = new LinkedList<>();
    }

    public List<DroneCommand> getCommandList() {
        return droneCommands;
    }

    public void setDroneCommands(LinkedList<DroneCommand> commands) {
        this.droneCommands = commands;

        int messageCount = this.droneCommands.stream().filter(MissionPoint.class::isInstance)
                .mapToInt(mission -> ((MissionPoint) mission).messageCount()).sum();

        Engine.platform().displayMessage("Item count: %d", messageCount);

        //Add all the needed drone commands for the drone to auto start the mission
        this.droneCommands.addFirst(MissionCountCommand.initMissionCountCommand(messageCount));

        this.droneCommands.addLast(SetCurrentCommand.initSetCurrentItem(0, 0, true));

        this.droneCommands.addLast(SetModeCommand.initSetMode(PLANE_MODE.PLANE_MODE_AUTO));
    }

    public int missionPoints() {
        return this.droneCommands.size();
    }

    public List<MAVLinkMessage> toMissionMessages(MAVLinkNode dest) {

        List<MAVLinkMessage> messages = new ArrayList<>();

        int current = 0;

        for (DroneCommand droneCommand : this.droneCommands) {
            if (!(droneCommand instanceof MissionPoint)) continue;

            Collection<MAVLinkMessage> collection = ((MissionPoint) droneCommand).toMavLinkMessage(dest, current);

            messages.addAll(collection);

            current += collection.size();

        }

        return messages;
    }

    public Map<Integer, List<MAVLinkMessage>> droneCommandsToMissionItem(MAVLinkNode dest) {

        int currentMissionPoint = 0;

        Map<Integer, List<MAVLinkMessage>> messages = new HashMap<>();

        for (DroneCommand droneCommand : this.droneCommands) {

            if (droneCommand instanceof MissionPoint) {
                currentMissionPoint++;
            } else {
                if (droneCommand.executeOnStartup()) {
                    List<MAVLinkMessage> commands = messages.getOrDefault(currentMissionPoint, new LinkedList<>());

                    commands.addAll(droneCommand.toMavLinkMessage(dest));

                    messages.put(currentMissionPoint, commands);
                }
            }

        }

        return messages;
    }

    @Override
    public Mission clone() {
        try {
            return (Mission) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static final Random random = new Random();

    public static Mission initializeMission() {
        return initializeMission("Task_ " + random.nextLong());
    }

    public static Mission initializeMission(String id) {
        return new Mission(id);
    }

    public Mission cloneMission(String newName) {

        Mission mission = new Mission(newName);

        mission.setDroneCommands(this.droneCommands);

        return mission;
    }

    @Override
    public List<NodeFilter> getRequirements() {
        List<NodeFilter> filters = new LinkedList<>();
        NodeFilter filter = new NodeFilter();

        filters.add(filter);
        return filters;
    }

    @Override
    public TaskExecutor getExecutor() {
        return new MissionExecutor(this);
    }

}
