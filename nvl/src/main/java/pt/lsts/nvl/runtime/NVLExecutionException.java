package pt.lsts.nvl.runtime;

@SuppressWarnings("serial")
public class NVLExecutionException extends RuntimeException {

  public NVLExecutionException() {

  }

  public NVLExecutionException(String message) {
    super(message);
  }

  public NVLExecutionException(Throwable cause) {
    super(cause);
  }

  public NVLExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public NVLExecutionException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
