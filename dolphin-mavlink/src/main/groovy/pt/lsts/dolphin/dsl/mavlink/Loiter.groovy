package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint

@DSLClass
class Loiter {

    MissionPoint initLoiterPoint(double lat, double lon, double hei) {
        return initLoiterPoint(PositionBuilder.initPosition(lat, lon, hei));
    }

    MissionPoint initLoiterPoint(Position position) {
        return LoiterPoint.initLoiterPoint(position);
    }

    MissionPoint initLoiterPoint(Position position, float radius) {
        return LoiterPoint.initLoiterPoint(position, radius);
    }

    MissionPoint initLoiterPoint(double lat, double lon, double hei, float radius) {
        return initLoiterPoint(PositionBuilder.initPosition(lat, lon, hei), radius);
    }

    private Loiter() {
    }

}
