
package pt.lsts.nvl.util.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * TCP server socket connection.
 * 
 * <p>
 * This message link allows simple bi-directional communication using a server
 * TCP socket.
 * </p>
 * 
 * <p>
 * Note that the connection may have multiple peers. Messages sent over the
 * connection will be sent to all peers.
 * 
 * @author Eduardo Marques
 */
public final class TCPServerLink implements NetworkLink {
  /**
   * Listening port.
   */
  private int                            _port;

  /**
   * Server channel for "select" operation.
   */
  private ServerSocketChannel            serverChannel;

  /**
   * Selector for "select" operation.
   */
  private Selector                       selector;

  /**
   * List of client SocketChannel handles.
   */
  private final ArrayList<SocketChannel> clients;

  /**
   * Constructor.
   * 
   * @param port
   *          server port to use
   */
  public TCPServerLink(int port) {
    this.clients = new ArrayList<SocketChannel>();
    this._port = port;
    serverChannel = null;
    selector = null;
  }

  @Override
  public void enable() throws NetworkLinkException {
    try {
      serverChannel = ServerSocketChannel.open();
      selector = Selector.open();
      serverChannel.configureBlocking(false);
      serverChannel.socket().setReuseAddress(true);
      serverChannel.socket().bind(new InetSocketAddress(_port));
      serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (Exception e) {
      if (serverChannel != null) {
        disable();
      }
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public void disable() throws NetworkLinkException {
    checkConnected();
    try {
      if (selector != null)
        selector.close();
      for (SocketChannel channel : clients) {
        try {
          channel.close();
        } catch (IOException e) {
          // Ignore error when closing client socket
        }
      }
      serverChannel.close();
    } catch (Exception e) {
      // Ignore
    }
    serverChannel = null;
    selector = null;
    clients.clear();
  }

  @Override
  public synchronized int recv(byte[] buf, int off, int len, int timeout)
      throws NetworkLinkException {
    checkConnected();
    int readData = 0;
    try {
      if (selector.select(timeout) == 0)
        return 0;
      Iterator<SelectionKey> it = selector.selectedKeys().iterator();
      while (it.hasNext()) {
        SelectionKey key = it.next();
        it.remove();
        if (!key.isValid()) {
          if (key.channel() instanceof SocketChannel) {
            key.channel().close();
            clients.remove(key.channel());
          }
        } else if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
          // New client connection
          SocketChannel channel = serverChannel.accept();
          if (channel == null)
            continue;
          channel.configureBlocking(false);
          channel.register(selector, SelectionKey.OP_READ);
          clients.add(channel);
        } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
          // Data available
          SocketChannel channel = (SocketChannel) key.channel();
          if (readData < len) {
            try {
              readData += channel.read(ByteBuffer.wrap(buf, off + readData, len
                  - readData));
            } catch (IOException e) {
              clients.remove(channel);
              System.err.println("Lost touch with " + channel + "??? -> "
                  + e.getMessage());
              channel.close();
            }
          }
        }
      }
      return readData;
    } catch (IOException e) {
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public synchronized void send(byte[] data, int off, int len)
      throws NetworkLinkException {
    checkConnected();
    Iterator<SocketChannel> itr = clients.iterator();
    while (itr.hasNext()) {
      SocketChannel channel = itr.next();
      try {
        channel.write(ByteBuffer.wrap(data, off, len));
      } catch (IOException e) {
        itr.remove();
        System.err.println("Lost touch with " + channel + "??? -> "
            + e.getMessage());
        try {
          channel.close();
        } catch (IOException e2) {
        }
      }
    }
  }

  @Override
  public boolean enabled() {
    return serverChannel != null;
  }

  @Override
  public void sendTo(byte[] data, int off, int len, InetAddress addr, int port)
      throws NetworkLinkException {
    throw new NetworkLinkException("Unsupported operation.");

  }

  private void checkConnected() throws NetworkLinkException {
    if (serverChannel == null)
      throw new NetworkLinkException("Not in connected state");
  }


}
