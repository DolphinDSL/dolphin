package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint

@DSLClass
class MissionBuilder extends Builder<Mission> {

    String name;

    LinkedList<MissionPoint> points;

    public MissionBuilder() {
        this.name = "MissionPlan_" + String.valueOf(System.currentTimeMillis());

        this.points = new LinkedList<>();
    }

    void name (String name) {
        this.name = name;
    }

    void point(MissionPoint point) {
        this.points.add(point);
    }

    @Override
    Mission build() {
        Mission m = Mission.initializeMission(name);

        m.setPoints(points);

        return m
    }
}
