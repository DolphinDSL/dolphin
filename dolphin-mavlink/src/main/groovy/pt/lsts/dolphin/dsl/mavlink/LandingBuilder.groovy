package pt.lsts.dolphin.dsl.mavlink

import com.MAVLink.enums.PRECISION_LAND_MODE
import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LandingPoint

@DSLClass
class LandingBuilder extends Builder<MissionPoint> {

    Position pos;

    void pos(Closure cl) {
        this.pos = new PositionBuilder().build(cl);
    }

    void pos(Position p) {
        this.pos = p
    }

    @Override
    MissionPoint build() {
        return LandingPoint.initLandingPoint(pos, Float.NaN, PRECISION_LAND_MODE.PRECISION_LAND_MODE_DISABLED);
    }
}
