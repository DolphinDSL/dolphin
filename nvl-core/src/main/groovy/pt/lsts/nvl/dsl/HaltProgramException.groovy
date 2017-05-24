package pt.lsts.nvl.dsl

class HaltProgramException extends RuntimeException {

  HaltProgramException(String message) {
    super(message);
  }
}
