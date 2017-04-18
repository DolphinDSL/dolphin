package pt.lsts.nvl.dsl;

@DSLClass
final class Requirements extends Instruction {
  Type type;
  String[] payload;

  void type(Type t) {
    type = t;
  }
  
  void payload(String... p) {
    println p
    payload = p;
  }

  @Override
  public void execute() {
    println "e " + toString()
  }
}