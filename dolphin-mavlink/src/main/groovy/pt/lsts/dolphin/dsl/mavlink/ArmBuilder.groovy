package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ArmCommand

@DSLClass
class ArmBuilder extends Builder<MissionPoint> {

    int armDisarm = 1;

    public ArmBuilder() {}

    void arm() {
        armDisarm = 1;
    }

    void disarm() {
        armDisarm = 0;
    }

    @Override
    MissionPoint build() {
        return null;
    }
}
