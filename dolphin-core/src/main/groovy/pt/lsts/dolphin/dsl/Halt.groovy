package pt.lsts.dolphin.dsl

@DSLClass
class Halt extends RuntimeException {

  Halt(String message) {
    super(message);
  }
}
