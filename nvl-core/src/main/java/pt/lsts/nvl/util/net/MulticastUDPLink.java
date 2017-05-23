
package pt.lsts.nvl.util.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Multicast UDP connection.
 * 
 * @author Eduardo Marques
 */
public final class MulticastUDPLink implements NetworkLink {
	/**
	 * UDP datagram socket.
	 */
	private MulticastSocket   socket;

	/**
	 * Datagram socket used for sending data (important for filtering purposes).
	 */
	private DatagramSocket   sendSocket;

	/**
	 * Multicast port.
	 */
	private final int         port;

	/**
	 * Multicast address to use.
	 */
	private final InetAddress address;


	/**
	 * Constructor.
	 * 
	 * @param maddr
	 *          Multicast IP address.
	 * @param port
	 *          Port.
	 * @throws NetworkLinkException
	 *           on error
	 */
	public MulticastUDPLink(String host, int port)
			throws NetworkLinkException,UnknownHostException {
    this(InetAddress.getByName(host), port);
	}

  public MulticastUDPLink(InetAddress address, int port) throws NetworkLinkException {
    this.address = address;
    this.port = port;
   
    if (!address.isMulticastAddress()) {
      throw new NetworkLinkException(address + ": not a multicast IP address");
    }
    socket = null;
  }

  @Override
	public synchronized void enable() throws NetworkLinkException {
		if (socket != null)
			throw new NetworkLinkException("Connection already opened");
		try {
			sendSocket = new DatagramSocket();
			socket = new MulticastSocket(port);
			socket.joinGroup(address);
			socket.setLoopbackMode(false);
			socket.setTimeToLive(5);
			sendSocket.setBroadcast(true);
		} catch (Exception e) {
			throw new NetworkLinkException(e);
		}
	}

  @Override
	public final boolean enabled() {
		return socket != null;
	}

  @Override
	public synchronized void disable() throws NetworkLinkException {
		if (socket == null)
			throw new NetworkLinkException("Connection already closed");
		try {
			socket.leaveGroup(address);
		} catch (IOException e) {

		}
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
			if (packet.getAddress().isLoopbackAddress() && packet.getPort() == sendSocket.getLocalPort())
				return 0; // filter out 
			return packet.getLength();
		} catch (SocketTimeoutException e) {
			return 0; // timeout
		} catch (SocketException e) {
			throw new NetworkLinkException(e);
		} catch (IOException e) {
			throw new NetworkLinkException(e);
		}
	}

	@Override
	public void send(byte[] data, int off, int len) throws NetworkLinkException {
		sendTo(data, off, len, address, port);
	}

	private void checkConnected() throws NetworkLinkException {
		if (socket == null)
			throw new NetworkLinkException("Not in connected state");
	}

	@Override
	public void sendTo(byte[] data, int off, int len, InetAddress addr, int port)
			throws NetworkLinkException {
		checkConnected();
		try {
			socket.send(new DatagramPacket(data, off, len, addr, port));
		} catch (IOException e) {
			throw new NetworkLinkException(e);
		}

	}

  
}
