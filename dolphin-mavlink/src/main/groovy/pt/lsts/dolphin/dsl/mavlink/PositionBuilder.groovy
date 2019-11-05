package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.util.wgs84.NED
import pt.lsts.dolphin.util.wgs84.WGS84

@DSLClass
class PositionBuilder {

    static Position initPosition(double latInDeg, double longInDeg, double alt) {
        return Position.fromDegrees(latInDeg, longInDeg, alt);
    }

    static Position movePosition(Position position, NED toMove) {
        return WGS84.displace(position, toMove);
    }

    static Position movePosition(Position position, double north, double east, double alt) {
        return movePosition(position, initMoveDistances(north, east, alt));
    }

    static NED initMoveDistances(double north, double east, double down) {
        return new NED(north, east, down);
    }

}
