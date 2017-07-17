package pt.lsts.dolphin.util.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * TCP client socket connection.
 * 
 * <p>
 * This message link allows simple bi-directional communication using a client
 * TCP socket connected to a specified remote TCP server socket.
 * </p>
 * 
 * @author Eduardo Marques
 */
public final class TCPLink implements NetworkLink {
  /**
   * Remote server port.
   */
  private final int         port;

  /**
   * Remote server address.
   */
  private final InetAddress address;

  /**
   * TCP socket.
   */
  private SocketChannel     channel;

  /**
   * Constructor.
   * 
   * @param host
   *          Remote host name or IP address
   * @param port
   *          Port number at remote host.
   * @throws NetworkLinkException
   *           for mal-formed host specification.
   */
  public TCPLink(String host, int port) throws NetworkLinkException {
    try {
      this.port = port;
      this.address = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public void enable() throws NetworkLinkException {
    try {
      channel = SocketChannel.open(new InetSocketAddress(address, port));
      channel.configureBlocking(false);
    } catch (Exception e) {
      if (channel != null) {
        disable();
      }
      throw new NetworkLinkException(e);
    }
  }

  private void checkConnected() throws NetworkLinkException {
    if (channel == null)
      throw new NetworkLinkException("Not in connected state");
  }

  @Override
  public void disable() throws NetworkLinkException {
    checkConnected();
    try {
      if (channel != null) {
        channel.close();
        channel = null;
      }
    } catch (IOException e) {
      // Ignore closing errors.
    }
  }

  @Override
  public int recv(byte[] buf, int off, int len, int timeout)
      throws NetworkLinkException {
    checkConnected();
    try {
      int n = 0;
      Selector selector = Selector.open();
      channel.register(selector, SelectionKey.OP_READ);
      if (selector.select(timeout) > 0)
        n = channel.read(ByteBuffer.wrap(buf, off, len));
      selector.close();
      return n;
    } catch (Exception e) {
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public void send(byte[] data, int off, int len) throws NetworkLinkException {
    checkConnected();
    try {
      channel.write(ByteBuffer.wrap(data, off, len));
    } catch (IOException e) {
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public boolean enabled() {
    return channel != null;
  }

  @Override
  public void sendTo(byte[] data, int off, int len, InetAddress addr, int port)
      throws NetworkLinkException {
    throw new NetworkLinkException("Unsupported operation.");

  }
}
