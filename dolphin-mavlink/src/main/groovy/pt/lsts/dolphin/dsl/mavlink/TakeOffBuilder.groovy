package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.TakeOffPoint

@DSLClass
class TakeOffBuilder extends Builder<MissionPoint> {

    private static final float DEFAULT_PITCH = 0.0f;
    private static final float DEFAULT_YAW = Float.NaN;

    Position pos;

    float pitch = DEFAULT_PITCH, yaw = DEFAULT_YAW;

    void pos(Closure cl) {
        this.pos = new PositionBuilder().build(cl);
    }

    void pos(Position pos) {
        this.pos = pos;
    }

    void pitch (float pitch) {
        this.pitch = pitch;
    }

    void yaw (float yaw) {
        this.yaw = yaw;
    }

    @Override
    MissionPoint build() {
        return TakeOffPoint.initTakeOffPoint(pos, pitch, yaw);
    }
}
