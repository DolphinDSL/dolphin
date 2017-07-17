package pt.lsts.dolphin.util.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * UDP socket connection.
 *
 * <p>This message link allows simple bi-directional communication  
 * between UDP ports. The local host will listen on a specified
 * local port for messages and send messages to a specified UDP
 * port on a remote host.</p>
 * 
 * @author Eduardo Marques
 */
public final class UDPLink implements NetworkLink {
  /**
   * UDP datagram socket.
   */
  private DatagramSocket socket;

  /**
   * Local UDP port for receiving data.
   */
  private int port;

  /**
   * Constructor for UDP connections with chosen port by the system.
   * @throws NetworkLinkException When host address is invalid. 
   */
  public UDPLink() throws NetworkLinkException {
    this(0);
  }

  /**
   * Constructor for UDP server connections.
   * @param local port
   * @throws NetworkLinkException When host address is invalid. 
   */
  public UDPLink(int port)
      throws NetworkLinkException {
    this.port = port;
    socket = null;
  }
  

  public int getPort() {
    return port;
  }
  
  @Override
  public synchronized void enable() throws NetworkLinkException {
    if(socket != null)
      throw new NetworkLinkException("Connection already opened!");
    try {
      if (port > 0) {
        socket = new DatagramSocket(port);
      } else {
        socket = new DatagramSocket();
        port = socket.getLocalPort();
      }
      socket.setReuseAddress(false);
    }catch(SocketException e){
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public final boolean enabled(){
    return socket != null;
  }

  @Override
  public synchronized void disable() throws NetworkLinkException {
    if(socket == null)
      throw new NetworkLinkException("Connection already closed");
    socket.close();
    socket = null;
  }

  @Override
  public int recv(byte[] buf, int off, int len, int timeout) 
      throws NetworkLinkException {
    checkConnected();
    try {
      DatagramPacket packet = new DatagramPacket(buf, off, len);
      socket.setSoTimeout(timeout); 
      socket.receive(packet);
      return packet.getLength();
    } 
    catch(SocketTimeoutException e){
      return 0; // timeout
    }
    catch(SocketException e){
      throw new NetworkLinkException(e);
    }
    catch(IOException e){
      throw new NetworkLinkException(e);
    }
  }

  @Override
  public void send(byte[] data, int off, int len) throws NetworkLinkException {
    throw new NetworkLinkException("Operation not supported!");
  }


  @Override
  public void sendTo(byte[] data, int off, int len, InetAddress addr, int port)
      throws NetworkLinkException {
    checkConnected();
    try {
      socket.send(new DatagramPacket(data, off, len, addr, port));
    } catch(IOException e){
      throw new NetworkLinkException(e);
    }
  }
  
  private void checkConnected() throws NetworkLinkException {
    if(socket == null)
      throw new NetworkLinkException("Not in connected state");
  }



}
