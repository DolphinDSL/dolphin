package pt.lsts.dolphin.runtime.tasks;

public final class CompletionState {
 
  public enum Type {
    UNDEFINED,
    IN_PROGRESS,
    DONE,
    ERROR
  }

  public final Type type;
  protected final String errorMessage;
  
  public CompletionState(Type type) {
    this(type, null);
  }
  
  public CompletionState(Type type, String info) {
    this.type = type;
    this.errorMessage = info;
  }
  
  /**
 * @return the data
 */
public String getErrorMesssage() {
	return errorMessage;
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
