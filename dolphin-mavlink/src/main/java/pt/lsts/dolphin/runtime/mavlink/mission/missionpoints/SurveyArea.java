package pt.lsts.dolphin.runtime.mavlink.mission.missionpoints;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_FRAME;
import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint;
import pt.lsts.dolphin.util.wgs84.NED;
import pt.lsts.dolphin.util.wgs84.WGS84;

import java.util.Collection;
import java.util.LinkedList;

public class SurveyArea extends MissionPoint {

    //20 meters for the drone to turn around when he reaches the end of the zone
    //5 meters for the drone to travel after leaving the area
    private static final double OVERTHROW = 25D;

    private double length, width, direction_change;

    private Direction direction;

    private SurveyArea(Position pointLocation, double length, double width, double direction_change, Direction direction) {
        super(pointLocation);

        this.length = Math.max(length, width);
        this.width = Math.min(length, width);

        this.direction = direction;
        this.direction_change = direction_change;

    }

    @Override
    public Collection<MAVLinkMessage> toMavLinkMessage(MAVLinkNode dest, int current) {

        LinkedList<MAVLinkMessage> messages = new LinkedList<>();

        int numberOfPoints = (int) Math.round(width / direction_change);

        Engine.platform().displayMessage("GENERATING %d POINTS FOR AREA", numberOfPoints);

        int currentDirection = 1;

        Position currentPos = getPositionLocation();

        for (int i = 0; i < numberOfPoints; i++) {

            //Make the length of the area
            NED next = new NED(direction.getMultNorth() * (length + OVERTHROW) * currentDirection,
                    direction.getMultEast() * (length + OVERTHROW) * currentDirection, 0);

            Position displace = WGS84.displace(currentPos, next);

            currentPos = displace;

            MAVLinkMessage goToLength = generateGoTo(dest, current + i++, displace);

            messages.addLast(goToLength);

            NED changeDirection = new NED(direction.getMultEast() * direction_change, direction.getMultNorth() * direction_change, 0);

            Position displaceLateral = WGS84.displace(currentPos, changeDirection);

            MAVLinkMessage goToLateral = generateGoTo(dest, current + i, displaceLateral);

            messages.addLast(goToLateral);

            currentPos = displaceLateral;

            currentDirection = -currentDirection;
        }

        Engine.platform().displayMessage("MESSAGES: %d", messages.size());

        MAVLinkMessage gotoHome = generateGoTo(dest, current + numberOfPoints, getPositionLocation());

        messages.addLast(gotoHome);

        return messages;
    }

    private MAVLinkMessage generateGoTo(MAVLinkNode node, int current, Position pos) {

        msg_mission_item msg_item = new msg_mission_item();

        msg_item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;

        msg_item.target_system = (short) node.getMAVLinkId();
        msg_item.target_component = 0;
        msg_item.frame = MAV_FRAME.MAV_FRAME_GLOBAL;
        msg_item.seq = current;
        msg_item.current = 0;
        msg_item.autocontinue = 1;
        msg_item.param1 = 0;
        msg_item.param2 = (float) OVERTHROW;
        msg_item.param3 = 0;
        msg_item.param4 = Float.NaN;

        msg_item.x = (float) (pos.lat * Position.R2D);
        msg_item.y = (float) (pos.lon * Position.R2D);
        msg_item.z = (float) pos.hae;

        return msg_item;

    }

    @Override
    public int messageCount() {
        return (int) Math.round(this.width / direction_change);
    }

    public enum Direction {

        NORTH(1, 0),
        SOUTH(-1, 0),
        EAST(0, 1),
        WEST(0, -1);

        private double multNorth, multEast;

        Direction(double multNorth, double multEast) {
            this.multNorth = multNorth;
            this.multEast = multEast;
        }

        public double getMultNorth() {
            return multNorth;
        }

        public double getMultEast() {
            return multEast;
        }
    }

    public static MissionPoint initAreaSurvey(Position base, double length, double width, double direction_change, Direction dir) {
        return new SurveyArea(base, length, width, direction_change, dir);
    }
}
