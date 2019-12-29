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

        int messageCount = (int) this.droneCommands.stream().filter(MissionPoint.class::isInstance).count();

        Engine.platform().displayMessage("Item count: %d", messageCount);

        //Add all the needed drone commands for the drone to auto start the mission
        this.droneCommands.addFirst(MissionCountCommand.initMissionCountCommand(messageCount));

        this.droneCommands.addLast(SetModeCommand.initSetMode(PLANE_MODE.PLANE_MODE_AUTO));

        this.droneCommands.addLast(SetCurrentCommand.initSetCurrentItem(0));
    }

    public int missionPoints() {
        return this.droneCommands.size();
    }

    public List<MAVLinkMessage> toMavLinkMessages(MAVLinkNode dest) {

        List<MAVLinkMessage> messages = new ArrayList<>();

        int current = 0;

        for (DroneCommand missionPoint : this.droneCommands) {

            if (missionPoint instanceof MissionPoint) {
                messages.add(((MissionPoint) missionPoint).toMavLinkMessage(dest, current++));
            } else {
                messages.add(missionPoint.toMavLinkMessage(dest));
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
        return initializeMission(String.valueOf(random.nextLong()));
    }

    public static Mission initializeMission(String id) {
        return new Mission(id);
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
