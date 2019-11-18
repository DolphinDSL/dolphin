package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.dsl.Engine
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ArmCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ChangeAltitude
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ChangeSpeed
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.DelayCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.GoToPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LandingPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint.LoiterType
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetHomeCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.TakeOffPoint
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

    void arm() {
        point(ArmCommand.initArmCommand(1))
    }

    void disarm() {
        point(ArmCommand.initArmCommand(0));
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
        this.speed = newSpeed;
        point(ChangeSpeed.initChangeSpeed(newSpeed, groundSpeed));
    }

    void altitude(double newAlt) {
        point(ChangeAltitude.initChangeAltitude(newAlt));
    }

    void home(double lat, double lon, double hae = 0d) {
        this.home = Position.fromDegrees(lat, lon, hae);

        point(SetHomeCommand.initSetHome(this.home));
    }

    void home(Position pos) {
        this.home = pos;

        point(SetHomeCommand.initSetHome(this.home));
    }

    void delay(long time) {
        point(DelayCommand.initDelayPoint(time));
    }

    void moveAndLoiterPos(double north, double east, double up, float radius = 15) {
        //TODO: Make move points relative to the last position or to the home position ?
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(this.home, move);

        point(LoiterPoint.initLoiterPoint(moved, radius));
    }

    void loiterPos(Position pos, float radius = 15) {
        point(LoiterPoint.initLoiterPoint(pos, radius))
    }

    void loiterPos(double lat, double lon, double hae, float radius = 15) {
        point(LoiterPoint.initLoiterPoint(lat, lon, hae, radius))
    }

    void loiterTurns(double lat, double lon, double hae, float radius = 15, int arg = 0) {
        point(LoiterPoint.initLoiterPoint(Position.fromDegrees(lat, lon, hae), LoiterType.TURNS, radius, arg));
    }

    void loiterTime(double lat, double lon, double hae, float radius = 15, int arg = 0) {
        point(LoiterPoint.initLoiterPoint(Position.fromDegrees(lat, lon, hae), LoiterType.TIME, radius, arg))
    }

    void landingPoint(double lat, double lon, double hae = 0) {
        point(LandingPoint.initLandingPoint(lat, lon, hae));
    }

    void landingPoint(Position pos) {
        point(LandingPoint.initLandingPoint(pos))
    }

    void takeOff(Position pos, float pitch = 0) {
        point(TakeOffPoint.initTakeOffPoint(pos, pitch, Float.NaN));
    }

    void takeOffYaw(Position pos, float pitch, float yaw = Float.NaN) {
        point(TakeOffPoint.initTakeOffPoint(pos, pitch, yaw));
    }

    @Override
    Mission build() {
        Mission m = Mission.initializeMission(name);

        m.setPoints(points);

        return m
    }
}
