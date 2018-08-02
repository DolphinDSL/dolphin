package pt.lsts.dolphin.runtime.tasks;

public final class CompletionState {
 
  public enum Type {
    UNDEFINED,
    IN_PROGRESS,
    DONE,
    ERROR
  }

  public final Type type;
  public final Object data;
  
  public CompletionState(Type type) {
    this(type, null);
  }
  
  public CompletionState(Type type, Object data) {
    this.type = type;
    this.data = data;
  }
  
  public boolean inProgress() {
    return type == Type.IN_PROGRESS;
  }
  
  public boolean undefined() {
    return type == Type.UNDEFINED;
  }
  
  public boolean done() {
    return type == Type.DONE;
  }
  
  public boolean error() {
    return type == Type.ERROR;
  }

  public boolean finished() {
    return done() || error();
  }
}
