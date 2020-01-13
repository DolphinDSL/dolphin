package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand;

import java.util.List;

public class ConditionalCommand extends DroneCommand {

    private Condition condition;

    private List<DroneCommand> ifTrue, ifFalse;

    public ConditionalCommand(Condition condition, List<DroneCommand> ifTrue, List<DroneCommand> ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public List<DroneCommand> getIfTrue() {
        return ifTrue;
    }

    public List<DroneCommand> getIfFalse() {
        return ifFalse;
    }

    @Override
    public boolean executeOnStartup() {
        return false;
    }

    public boolean testCondition(MAVLinkNode node) {
        return this.condition.testCondition(node);
    }

    @Override
    public MAVLinkMessage toMavLinkMessage(MAVLinkNode dest) {
        return null;
    }

    private static abstract class Condition {

        abstract boolean testCondition(MAVLinkNode node);

    }

    private static class BatteryPower extends Condition {

        private int requiredBattery;

        public BatteryPower(int requiredBattery) {
            this.requiredBattery = requiredBattery;
        }

        @Override
        boolean testCondition(MAVLinkNode node) {

            byte battery_remaining = node.getLastSystemStatus().battery_remaining;

            if (battery_remaining < 0) {
                //Battery remaining is not being sent by auto pilot, assume battery is good

                return true;
            }

            return battery_remaining >= requiredBattery;
        }
    }
}
