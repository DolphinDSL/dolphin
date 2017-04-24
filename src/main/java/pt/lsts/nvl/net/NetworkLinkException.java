package pt.lsts.nvl.net;

import java.io.IOException;

/**
 * Connection exception, used by this package to signal connection related
 * errors.
 * 
 * @author Eduardo Marques
 */
@SuppressWarnings("serial")
public final class NetworkLinkException extends IOException {
  /**
   * Constructor with no arguments.
   */
  public NetworkLinkException() {
    super("Unknown connection error");
  }

  /**
   * Constructor with a cause for the error.
   * 
   * @param cause
   *          Error.
   */
  public NetworkLinkException(String cause) {
    super(cause);
  }

  /**
   * Constructor with nested exception.
   * 
   * @param cause
   *          Exception describing the error.
   */
  public NetworkLinkException(Exception cause) {
    super(cause);
  }
}
