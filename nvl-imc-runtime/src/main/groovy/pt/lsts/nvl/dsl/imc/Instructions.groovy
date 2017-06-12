package pt.lsts.nvl.dsl.imc

import groovy.lang.Closure
import pt.lsts.imc.IMCMessage
import pt.lsts.nvl.dsl.DSLClass
import pt.lsts.nvl.runtime.imc.IMCPlanTask
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
	  println "Plan Specification: "+ps.getPlanId()+" created"
	  new IMCPlanTask(ps)

  }
  
//  static void sendMessage(NodeSet nodes, IMCMessage message) {
//	 for (Node n : nodes) {
//	   NeptusPlatform.INSTANCE.displayMessage 'Sending \'%s\' to \'%s\'',
//											   message.getAbbrev(),
//											   n.getId()
//	   ImcMsgManager.getManager().sendMessageToSystem message,
//													  n.getId()
//	 }
//  }
//	 static void storePlan(NodeSet nodes, IMCPlanTask task) {
//		 def message = new PlanDB(TYPE.REQUEST,OP.SET,IMCSendMessageUtils.getNextRequestId(),task.id,task.getPlanSpecification(),"NVL Task")
//
//				  
//		 for (Node n : nodes) {
//		   NeptusPlatform.INSTANCE.displayMessage 'Sending \'%s\' to \'%s\'',
//												   task.id,
//												   n.getId()
//		   ImcMsgManager.getManager().sendMessageToSystem message,
//														  n.getId()
//		 }
//  }
//  
//	 private Instructions() {
//	 
//   }
	 
  static main(args) {
	println 'Runtime language extensions loaded!'
  }
  
}
