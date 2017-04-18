package pt.lsts.nvl.dsl;

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import java.util.List;
import pt.lsts.nvl.runtime.TaskSpecification
import pt.lsts.nvl.runtime.VehicleRequirements

@DSLClass
final class Task extends Instruction<Void> {
  TaskSpecification spec

  Task(String id) {
    spec = Engine.getInstance().getRuntime().getTask(id)
  }
  
  @Override
  public Void execute() {
    println "e " + toString()
  }
}

