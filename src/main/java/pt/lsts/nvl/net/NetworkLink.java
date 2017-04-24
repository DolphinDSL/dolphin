package pt.lsts.nvl.net;

import java.net.InetAddress;

/**
 * Network link.
 * 
 * @author Eduardo Marques
 */
interface NetworkLink {

  /**
   * Enable the link. 
   * 
   * @throws NetworkLinkException on connection setup error, or if already enabled.
   */
  void enable() throws NetworkLinkException;

  /**
   * Disable the link.
   * 
   * @throws NetworkLinkException on disconnect error, or if already disabled.
   */
  void disable() throws NetworkLinkException;

  /**
   * Indicates if link is enabled.
   * 
   * @return <code>true</code> if enabled.
   */
  boolean enabled();

  /**
   * Send data (optional, possibly unsupported).
   * 
   * @param data
   *          Data buffer.
   * @param off
   *          Offset in buffer.
   * @param len
   *          Length after offset position.
   * @throws NetworkLinkException
   *           When an error occurs, or connection is closed.
   */
  void send(byte[] data, int off, int len) throws NetworkLinkException;

  
  /**
   * Send data to a given destination (optional, possibly unsupported).
   * 
   * @param data
   *          Data buffer.
   * @param off
   *          Offset in buffer.
   * @param len
   *          Length after offset position.
   * @param address
   *          IP Address of destination.
   * @param port
   *          Destination port.
   * @throws NetworkLinkException
   *           When an error occurs, or connection is closed.
   */
  void sendTo(byte[] data, int off, int len, InetAddress addr, int port) throws NetworkLinkException;
  
  /**
   * Receive data.
   * 
   * @param buf
   *          the receive buffer to use
   * @param off
   *          the receive buffer offset
   * @param len
   *          the receive buffer usable length
   * @param timeout
   *          the time to wait for a message
   * @return the length of the received message packet or 0 in case timeout has
   *         been reached
   * @throws NetworkLinkException
   *           if an error occurs
   */
  int recv(byte[] buf, int off, int len, int timeout) throws NetworkLinkException;
}
