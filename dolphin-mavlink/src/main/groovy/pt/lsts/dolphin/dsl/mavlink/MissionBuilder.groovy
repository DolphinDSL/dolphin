package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ChangeAltitude
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ChangeSpeed
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.GoToPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetHomeCommand
import pt.lsts.dolphin.util.wgs84.NED
import pt.lsts.dolphin.util.wgs84.WGS84

@DSLClass
class MissionBuilder extends Builder<Mission> {

    String name;

    LinkedList<MissionPoint> points;

    private Position home;

    private double speed;

    public MissionBuilder() {
        this.name = "MissionPlan_" + String.valueOf(System.currentTimeMillis());

        this.points = new LinkedList<>();
    }

    void name(String name) {
        this.name = name;
    }

    void point(MissionPoint point) {
        this.points.add(point);
    }

    void goPos(double lat, double lon, double hae = 0d) {
        point(GoToPoint.initGoToPoint(lat, lon, hae));
    }

    void move(double north, double east, double up = 0d) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(home, move);

        point(GoToPoint.initGoToPoint(moved));
    }

    void speed(double newSpeed, boolean groundSpeed = false) {
        point(ChangeSpeed.initChangeSpeed(newSpeed, groundSpeed));
    }

    void altitude(double newAlt) {
        point(ChangeAltitude.initChangeAltitude(newAlt));
    }

    void home(double lat, double lon, double hae = 0d) {

        this.home = Position.fromDegrees(lat, lon, hae);
        point(SetHomeCommand.initSetHome(this.home));

    }


    @Override
    Mission build() {
        Mission m = Mission.initializeMission(name);

        m.setPoints(points);

        return m
    }
}
