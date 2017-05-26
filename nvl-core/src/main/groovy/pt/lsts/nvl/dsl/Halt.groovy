package pt.lsts.nvl.dsl

@DSLClass
class Halt extends RuntimeException {

  Halt(String message) {
    super(message);
  }
}
