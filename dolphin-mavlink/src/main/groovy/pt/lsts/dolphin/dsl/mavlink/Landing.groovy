package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LandingPoint

@DSLClass
class Landing {

    static MissionPoint land(Position position) {
        return LandingPoint.initLandingPoint(position);
    }

    static MissionPoint land(double lat, double lon, double hae) {
        return LandingPoint.initLandingPoint(lat, lon, hae);
    }

    private Landing() {

    }
}
