package pt.lsts.dolphin.dsl.mavlink

import com.MAVLink.Messages.MAVLinkMessage
import pt.lsts.dolphin.dsl.DSLClass
import pt.lsts.dolphin.dsl.Engine
import pt.lsts.dolphin.runtime.Node
import pt.lsts.dolphin.runtime.NodeSet
import pt.lsts.dolphin.runtime.Position
import pt.lsts.dolphin.runtime.mavlink.*
import pt.lsts.dolphin.runtime.mavlink.mission.MissionPoint

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

  private Instructions() {

  }
}

