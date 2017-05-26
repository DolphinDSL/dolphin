package pt.lsts.nvl.dsl

@DSLClass
class HaltProgramException extends RuntimeException {

  HaltProgramException(String message) {
    super(message);
  }
}
