package pt.lsts.dolphin.dsl.mavlink


import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.dsl.Engine
import pt.lsts.dolphin.runtime.Node
import pt.lsts.dolphin.runtime.NodeSet
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint.PointType
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.GoToPoint

@DSLClass
class GoTo {
    static MissionPoint goTo(Position position) {
        return GoToPoint.initGoToPoint(position);
    }

    static MissionPoint goTo(double latInDeg, double longInDeg, double alt) {
        return GoToPoint.initGoToPoint(latInDeg, longInDeg, alt);
    }

    static MissionPoint goTo(double latInDeg, double longInDeg, double alt, PointType type) {
        goTo(latInDeg, longInDeg, alt).withPointType(type);
    }

    static void goTo(NodeSet nodes, Position position) {

        for (Node n : nodes) {

            Mission m = MissionBuilder.initMissionBuilder().andThen(goTo(position)).build();

            attemptToGiveMissionToNode(n, m);
        }

    }

    static void goTo(NodeSet nodes, double latInDeg, double longInDeg, double alt) {

        for (Node n : nodes) {

            Mission m = MissionBuilder.initMissionBuilder().andThen(goTo(latInDeg, longInDeg, alt)).build();

            attemptToGiveMissionToNode(n, m)

        }

    }

    static void goTo(NodeSet nodes, double latInDeg, double longInDeg, double alt, PointType type) {

        for (Node n : nodes) {

            Mission m = MissionBuilder.initMissionBuilder().andThen(goTo(latInDeg, longInDeg, alt, type)).build();

            attemptToGiveMissionToNode(n, m)

        }

    }

    //TODO: Handle side missions, like taking pictures

    private static void attemptToGiveMissionToNode(Node destination, Mission mission) {
        Engine.platform().
                displayMessage("Sending goto command to node %s",
                        destination.getId());

        MAVLinkNode node = (MAVLinkNode) destination;

        node.getUploadProtocol().start(mission);

        Engine.platform().displayMessage("Attempted to send mission to drone %s" , destination.getId())
    }

    private GoTo() {}

}
