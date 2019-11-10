package pt.lsts.dolphin.dsl.mavlink

import com.MAVLink.Messages.MAVLinkMessage
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.dsl.Engine
import pt.lsts.dolphin.runtime.Node
import pt.lsts.dolphin.runtime.NodeSet
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.*
import pt.lsts.dolphin.runtime.mavlink.mission.Mission
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint
import pt.lsts.dolphin.runtime.mavlink.mission.missionpoints.LoiterPoint
import pt.lsts.dolphin.util.wgs84.NED
import pt.lsts.dolphin.util.wgs84.WGS84

import java.util.Random
import pt.lsts.imc.groovy.dsl.*

@DSLClass
class Instructions {

    static void sendMessage(NodeSet nodes, MAVLinkMessage message) {
        for (Node n : nodes) {
            Engine.platform().displayMessage 'Sending message \'%s\' (%d) to node \'%s\'',
                    message.getClass().getSimpleName(), message.msgid, n.getId()
            ((MAVLinkNode) n).send message
        }
    }

    static Mission mission(Closure cl) {
      return new MissionBuilder().build(cl);
    }

    static MissionPoint loiter(Closure cl) {
        new LoiterBuilder().build(cl);
    }

    static MissionPoint goto(Closure cl) {
        new GoToBuilder().build(cl);
    }

    static MissionPoint land(Closure cl) {
        new LandingBuilder().build(cl);
    }

    static MissionPoint takeOff(Closure cl) {
        new TakeOffBuilder().build(cl);
    }

    static Position pos(Closure cl) {
        new PositionBuilder().build(cl);
    }

    static Position move(Position p, NED distance) {
        return WGS84.displace(p, distance);
    }

    static Position move(Position p, Closure cl) {
        return move(p, moveDistance (cl));
    }

    static NED moveDistance(Closure cl) {
        return new MoveBuilder().build(cl);
    }

    private Instructions() {
    }
}

