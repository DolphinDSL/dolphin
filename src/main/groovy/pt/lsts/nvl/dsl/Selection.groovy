package pt.lsts.nvl.dsl;

import java.util.List;

import groovy.lang.Closure;

@DSLClass
final class Selection extends Instruction {
  double time = 0;
  List<Requirements> req = [];

  void time (double arg) {
    time = arg;
  }

  void vehicle(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Requirements) Closure cl) {
    def r = new Requirements()
    r.buildAndExecute cl
    req.add r
  }

  @Override
  public void execute() {
    println "e " + toString()
  }
}

