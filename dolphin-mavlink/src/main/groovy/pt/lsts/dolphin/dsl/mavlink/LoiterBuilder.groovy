package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint

@DSLClass
class LoiterBuilder extends Builder<MissionPoint> {

    Position pos;

    float radius = 0;

    void pos (Closure cl) {
        this.pos = new PositionBuilder().build(cl);
    }

    void pos (Position pos) {
        this.pos = pos;
    }

    void radius (float rad) {
        this.radius = rad
    }

    @Override
    MissionPoint build() {
        return LoiterPoint.initLoiterPoint(pos, radius);
    }
}
