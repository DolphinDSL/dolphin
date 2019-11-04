package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.TakeOffPoint

@DSLClass
class TakeOff {

    static MissionPoint takeOff(double lat, double lon, double hae) {
        return TakeOffPoint.initTakeOffPoint(lat, lon, hae);
    }

    static MissionPoint takeOff(Position pos) {
        return TakeOffPoint.initTakeOffPoint(pos);
    }

    static MissionPoint takeOff(Position pos, float pitch) {
        return TakeOffPoint.initTakeOffPoint(pos, pitch);
    }

    static MissionPoint takeOff(Position pos, float pitch, float yaw) {
        return TakeOffPoint.initTakeOffPoint(pos, pitch, yaw);
    }

    private TakeOff() {

    }

}
