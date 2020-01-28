package pt.lsts.dolphin.dsl.mavlink

import com.MAVLink.enums.PRECISION_LAND_MODE
import pt.lsts.dolphin.dsl.Builder
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.dsl.Engine
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.mission.DroneCommand
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ArmCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.CameraPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ChangeAltitude
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.ChangeSpeed
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.DelayCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.GoToPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LandingPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint.LoiterType
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetCurrentCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetHomeCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SetModeCommand
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.StopCameraCapture
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SurveyArea
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.SurveyArea.Direction
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.TakeOffPoint
import pt.lsts.dolphin.util.wgs84.NED
import pt.lsts.dolphin.util.wgs84.WGS84

@DSLClass
class MissionBuilder extends Builder<Mission> {

    String name;

    LinkedList<DroneCommand> points;

    LinkedList<Position> positions;

    private Position home = Position.fromDegrees(0, 0, 0);

    private double speed;

    MissionBuilder() {
        this.name = "MissionPlan_" + String.valueOf(System.currentTimeMillis());

        this.points = new LinkedList<>();
        this.positions = new LinkedList<>();
    }

    private void addPosition(Position p) {
        this.positions.add(p)

        if (this.home == null) {
            this.home = p;
        }
    }

    private Position getLastKnownPosition() {
        if (this.positions.size() == 0) {
            if (this.home != null) return this.home;

            else return Position.fromDegrees(0, 0, 0);
        }


        return this.positions.getLast();
    }

    void name(String name) {
        this.name = name;
    }

    private void point(DroneCommand point) {
        this.points.add(point);
    }

    void goPos(double lat, double lon, double hae = 0d) {

        addPosition(Position.fromDegrees(lat, lon, hae));

        point(GoToPoint.initGoToPoint(lat, lon, hae));
    }

    void move(double north, double east, double up = 0d) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(getLastKnownPosition(), move);

        addPosition(moved);

        point(GoToPoint.initGoToPoint(moved));
    }

    void moveFromHome(double north, double east, double up) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(home, move);

        addPosition(moved);

        point(GoToPoint.initGoToPoint(moved));
    }

    void moveAndLoiterPos(double north, double east, double up, float radius = 15) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(getLastKnownPosition(), move);

        addPosition(moved);

        point(LoiterPoint.initLoiterPoint(moved, LoiterType.UNLIM, radius, 0));
    }

    void moveAndLoiterTurns(double north, double east, double up, int turns = 10, float radius = 15) {

        def move = new NED(north, east, -up);

        def moved = WGS84.displace(getLastKnownPosition(), move);

        addPosition(moved);

        point(LoiterPoint.initLoiterPoint(moved, LoiterType.TURNS, radius, turns))
    }

    void moveAndLoiterTime(double north, double east, double up, int time, float radius = 15) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(getLastKnownPosition(), move);

        addPosition(moved);

        point(LoiterPoint.initLoiterPoint(moved, LoiterType.TIME, radius, time));
    }

    void moveAndLoiterFromHome(double north, double east, double up, float radius = 15) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(this.home, move);

        addPosition(moved);

        point(LoiterPoint.initLoiterPoint(moved, LoiterType.UNLIM, radius, 0));
    }

    void moveAndLoiterTurnsFromHome(double north, double east, double up, int turns = 10, float radius = 15) {

        def move = new NED(north, east, -up);

        def moved = WGS84.displace(this.home, move);

        addPosition(moved);

        point(LoiterPoint.initLoiterPoint(moved, LoiterType.TURNS, radius, turns));
    }

    void moveAndLoiterTimeFromHome(double north, double east, double up, int time, float radius = 15) {
        def move = new NED(north, east, -up);

        def moved = WGS84.displace(this.home, move);

        addPosition(moved);

        point(LoiterPoint.initLoiterPoint(moved, LoiterType.TIME, radius, time));
    }

    void returnHome() {

        def next = this.home;

        addPosition(next);

        point(GoToPoint.initGoToPoint(next));
    }

    void returnHomeAndLoiter(float radius = 15) {
        def next = this.home;

        addPosition(next);

        point(LoiterPoint.initLoiterPoint(next, radius));
    }

    void returnHomeAndLand(float yaw = Float.NaN, int landMode = PRECISION_LAND_MODE.PRECISION_LAND_MODE_DISABLED) {
        def next = this.home;

        addPosition(next);

        point(LandingPoint.initLandingPoint(next, yaw, landMode));
    }

    void speed(double newSpeed, int speedType = 1) {
        this.speed = newSpeed;

        point(ChangeSpeed.initChangeSpeed(newSpeed, speedType));
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

    void loiterPos(Position pos, float radius = 15) {
        point(LoiterPoint.initLoiterPoint(pos, radius))
    }

    void loiterPos(double lat, double lon, double hae, float radius = 15) {
        point(LoiterPoint.initLoiterPoint(lat, lon, hae, radius))
    }

    void loiterTurns(double lat, double lon, double hae, int turns = 10, float radius = 15) {
        point(LoiterPoint.initLoiterPoint(Position.fromDegrees(lat, lon, hae), LoiterType.TURNS, radius, turns));
    }

    void loiterTime(double lat, double lon, double hae, int time, float radius = 15) {
        point(LoiterPoint.initLoiterPoint(Position.fromDegrees(lat, lon, hae), LoiterType.TIME, radius, time))
    }

    void landingPoint(double lat, double lon, double hae = 0, float yaw = Float.NaN, int landMode = PRECISION_LAND_MODE.PRECISION_LAND_MODE_DISABLED) {
        point(LandingPoint.initLandingPoint(lat, lon, hae, yaw, landMode));
    }

    void landingPoint(Position pos, float yaw = Float.NaN, int landMode = PRECISION_LAND_MODE.PRECISION_LAND_MODE_DISABLED) {
        point(LandingPoint.initLandingPoint(pos, yaw, landMode))
    }

    void takeOff(Position pos, float pitch = 15) {
        point(TakeOffPoint.initTakeOffPoint(pos, pitch, Float.NaN));
    }

    void takeOffWithYaw(Position pos, float pitch, float yaw = Float.NaN) {
        point(TakeOffPoint.initTakeOffPoint(pos, pitch, yaw));
    }

    void capturePhoto() {
        point(CameraPoint.initCameraPoint(CameraPoint.CameraType.ONE_PHOTO, 1, 0));
    }

    void captureSeveralPhotos(int photos, int timeBetween = 2) {
        point(CameraPoint.initCameraPoint(CameraPoint.CameraType.IMAGE_COUNT, photos, timeBetween));
    }

    void captureUntilStop(int timeBetween = 2) {
        point(CameraPoint.initCameraPoint(CameraPoint.CameraType.UNTIL_STOP, 0, timeBetween));
    }

    void stopCapturing() {
        point(StopCameraCapture.initStopCapture());
    }

    void jumpToItem(int item, int repetitions = 1) {

        point(SetCurrentCommand.initSetCurrentItem(item, repetitions));

    }

    void surveyArea(Position pos, double length, double width, String dir = "NORTH") {

        addPosition(pos);

        point(SurveyArea.initAreaSurvey(pos, length, width, Direction.valueOf(dir)));

    }

    void surveyArea(double length, double width, String dir = "NORTH") {
        surveyArea(getLastKnownPosition(), length, width, dir);
    }

    @Override
    Mission build() {
        Mission m = Mission.initializeMission(name);

        m.setDroneCommands(points);

        return m
    }
}
