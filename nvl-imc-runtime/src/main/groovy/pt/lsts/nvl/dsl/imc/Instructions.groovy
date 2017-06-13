package pt.lsts.nvl.dsl.imc

import pt.lsts.imc.IMCMessage
import pt.lsts.nvl.dsl.DSLClass
import pt.lsts.nvl.dsl.Engine
import pt.lsts.nvl.runtime.Node
import pt.lsts.nvl.runtime.NodeSet
import pt.lsts.nvl.runtime.imc.IMCCommunications
import pt.lsts.nvl.runtime.imc.IMCNode
import pt.lsts.nvl.runtime.imc.IMCPlanTask
import pt.lsts.nvl.runtime.imc.IMCPlatform

import java.util.Random
import pt.lsts.imc.groovy.dsl.*

@DSLClass
class Instructions {

  static IMCPlanTask imcPlan(String id) {
    new IMCPlanTask(id)
  }

  static IMCPlanTask imcPlan(Closure cl) {
    def random_name = "DSLPlan_"+Integer.toString( Math.abs(new Random(System.currentTimeMillis()).nextInt()))
    def dslPlan = new DSLPlan(random_name)

    def code = cl.rehydrate(dslPlan, this, this)
    code.resolveStrategy = Closure.DELEGATE_ONLY
    code()
    def ps = dslPlan.asPlanSpecification()
    new IMCPlanTask(ps)

  }

  static void sendMessage(NodeSet nodes, IMCMessage message) {
    for (Node n : nodes) {
      Engine.platform().displayMessage 'Sending \'%s\' to \'%s\'',
          message.getAbbrev(),
          n.getId()
      IMCNode imcNode = (IMCNode) n
      imcNode.send message
    }
  }

  private Instructions() {

  }

}
