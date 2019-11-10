package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionExecutor
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint

@DSLClass
class MissionBuilder {

    static MissionBuilder initMissionBuilder() {
        return new MissionBuilder("MISSIONPLAN_" + String.valueOf(System.currentTimeMillis()));
    }

    private String name;

    private LinkedList<MissionPoint> points;

    private MissionBuilder(String name) {
        this.name = name;
        this.points = new LinkedList<>();
    }

    public MissionBuilder andThen(Closure cl) {

        

        return this;
    }

    public Mission build() {
        return Mission.initializeMission(name).setPoints(points);
    }

}
