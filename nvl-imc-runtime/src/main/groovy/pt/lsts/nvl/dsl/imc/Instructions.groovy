package pt.lsts.nvl.dsl.imc

import pt.lsts.nvl.dsl.DSLClass
import pt.lsts.nvl.runtime.imc.IMCPlanTask

@DSLClass
class Instructions {

  static IMCPlanTask imcPlan(String id) {
    new IMCPlanTask(id)
  }
  
  private Instructions() {
    
  }
}
