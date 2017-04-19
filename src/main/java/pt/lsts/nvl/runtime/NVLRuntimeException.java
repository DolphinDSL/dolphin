package pt.lsts.nvl.runtime;

@SuppressWarnings("serial")
public class NVLRuntimeException extends RuntimeException {

  public NVLRuntimeException() {

  }

  public NVLRuntimeException(String message) {
    super(message);
  }

  public NVLRuntimeException(Throwable cause) {
    super(cause);
  }

  public NVLRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public NVLRuntimeException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
