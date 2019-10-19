package pt.lsts.dolphin.dsl.mavlink

import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.dsl.Engine
import pt.lsts.dolphin.runtime.Node
import pt.lsts.dolphin.runtime.NodeSet
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.MAVLinkNode
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint

@DSLClass
class GoTo {

    static MissionPoint goTo(Position position) {
        return MissionPoint.initGoToPoint(position);
    }

    static MissionPoint goTo(double latInDeg, double longInDeg, double alt) {
        return MissionPoint.initGoToPoint(latInDeg, longInDeg, alt);
    }

    static void goTo(NodeSet nodes, Position position) {

        for (Node n : nodes) {
            Mission m = Mission.initializeMission()
                    .addPoint(goTo(position));

            attemptToGiveMissionToNode(n, m);
        }

    }

    static void goTo(NodeSet nodes, double latInDeg, double longInDeg, double alt) {

        for (Node n : nodes) {

            Mission m = Mission.initializeMission()
                    .addPoint(goTo(latInDeg, longInDeg, alt));

            attemptToGiveMissionToNode(n, m)

        }

    }

    private static void attemptToGiveMissionToNode(Node destination, Mission mission) {
        Engine.platform().
                displayMessage("Sending goto command to node %s",
                        destination.getId());

        MAVLinkNode node = (MAVLinkNode) destination;

        if (node.startMission(mission)) {
            Engine.platform()
                    .displayMessage("Successfully started drone %s on the mission.",
                            node.getId())
        } else {

            Engine.platform()
                    .displayMessage("Failed to start drone %s on the mission, the drone might be executing another mission at this moment",
                            node.getId());

        }
    }

    private GoTo() {}

}
