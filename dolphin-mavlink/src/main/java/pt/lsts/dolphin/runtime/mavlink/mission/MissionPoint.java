package pt.lsts.dolphin.runtime.mavlink.mission;

import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode;

public class MissionPoint {

    private Position positionLocation;

    private PointPayload payload;

    private PointType pointType;

    private MissionPoint(Position pointLocation, PointPayload payload, PointType pointType) {
        this.positionLocation = pointLocation;
        this.payload = payload;
        this.pointType = pointType;
    }

    public MissionPoint withPointType(PointType pointType) {
        this.pointType = pointType;
        return this;
    }

    public MissionPoint withPayload(PointPayload pointPayload) {
        this.payload = pointPayload;

        return this;
    }

    public void sendTo(MAVLinkNode node) {
        //TODO: Send position
        //TODO: send payload

    }

    public enum PointType {

        WAYPOINT,
        LOITER,
        LOITER_DIST,
        LOITER_TIME
        /**
         * TODO: Add more modes
         */

    }

    /**
     * Create a simple goto mission point for the drone to follow
     *
     * @param latInDeg The latitude of the point in degrees
     * @param longInDeg The longitude of the point in degrees
     * @param haeInDeg The height of the position
     * @return The initialized MissionPoint
     */
    public static MissionPoint initGoToPoint(double latInDeg, double longInDeg, double haeInDeg) {
        return initGoToPoint(Position.fromDegrees(latInDeg, longInDeg, haeInDeg));
    }

    public static MissionPoint initGoToPoint(Position position) {
        return new MissionPoint(position, null, PointType.WAYPOINT);
    }

}
