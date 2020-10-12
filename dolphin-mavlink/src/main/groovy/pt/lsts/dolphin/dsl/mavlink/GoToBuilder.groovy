package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.GoToPoint

@DSLClass
class GoToBuilder extends Builder<MissionPoint> {

    Position pos;

    void pos (Closure cl) {
        this.pos = new PositionBuilder().build(cl);
    }

    void pos (Position p) {
        this.pos = p;
    }

    @Override
    MissionPoint build() {
        return GoToPoint.initGoToPoint(pos);
    }
}
