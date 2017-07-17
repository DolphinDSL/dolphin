package pt.lsts.dolphin.runtime.imc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import pt.lsts.dolphin.runtime.EnvironmentException;
import pt.lsts.dolphin.runtime.NodeSet;
import pt.lsts.dolphin.util.Clock;
import pt.lsts.dolphin.util.Debuggable;
import pt.lsts.dolphin.util.net.MulticastUDPLink;
import pt.lsts.dolphin.util.net.NetworkInterfaces;
import pt.lsts.dolphin.util.net.NetworkLink;
import pt.lsts.dolphin.util.net.NetworkLinkException;
import pt.lsts.dolphin.util.net.UDPLink;
import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.def.SystemType;

public class IMCCommunications extends Thread implements Debuggable {

  static {
    try {
      ANNOUNCE_MCAST_ADDR = InetAddress.getByName("224.0.75.69");
    } catch (UnknownHostException e) {
      throw new EnvironmentException(e);
    }
  }

  private static final InetAddress ANNOUNCE_MCAST_ADDR;
  private static final int FIRST_ANNOUNCE_PORT = 30100;
  private static final int LAST_ANNOUNCE_PORT = 30110;
  private static final double ANNOUNCE_PERIOD = 10;
  private static final double HEARTBEAT_PERIOD = 1;
  private static final double CONNECTION_TIMEOUT = 3 * ANNOUNCE_PERIOD;
  private static final int FIRST_MSG_PORT = 6001;
  private static final int LAST_MSG_PORT = 6020;

  private final Announce announceMsg = new Announce();
  private final Heartbeat heartbeatMsg = new Heartbeat();
  private final byte[] rcvBuffer = new byte[16384];
  private final Map<Integer,IMCNode> nodes = new HashMap<>();

  private MulticastUDPLink announceLink;
  private UDPLink messageLink;

  private boolean active;
  private double timeOfStep;

  private static IMCCommunications INSTANCE = null;

  public static IMCCommunications getInstance() { 
    if (INSTANCE == null) {
      INSTANCE = new IMCCommunications();
    }
    return INSTANCE;
  }

  private IMCCommunications() {
    super("IMC communications");
    //setDaemon(true);
    setupLinks();
    setupIdentification();
    active = true;
  }


  @SuppressWarnings("deprecation")
  public void terminate() {

    if (active && isAlive()) {
      try {
        join(10);
      } catch(InterruptedException e) {

      }
      if (isAlive()) {
        stop();
      }
      teardownLinks();
    }
    active = false;
  }

  public void run() {
    while (active) {
      try {
        timeOfStep = Clock.now();
        sendAnnounce();
        sendHeartbeats();
        handleIncomingMessages(announceLink);
        handleIncomingMessages(messageLink);
        checkForLostConnections();
      } catch(Throwable e) {
        break;
      }
    }
  }

  private void checkForLostConnections() {
    Iterator<IMCNode> itr = nodes.values().iterator();
    while (itr.hasNext()) {
      IMCNode vehicle = itr.next();
      if (vehicle.getRunningTask() != null && vehicle.timeOfLastMessage() - timeOfStep >= CONNECTION_TIMEOUT) {
        itr.remove();
      }
    }
  }

  private void sendAnnounce() {
    if (timeOfStep - announceMsg.getTimestamp() < ANNOUNCE_PERIOD) {
      return;
    }
    announceMsg.setTimestamp(timeOfStep);
    for (int p = FIRST_ANNOUNCE_PORT; p <= LAST_ANNOUNCE_PORT; p++) {
      send(announceLink, announceMsg, ANNOUNCE_MCAST_ADDR, p);
      // sendMessage(announceMsg, "255.255.255.255", p);
    }
    for (IMCNode p : nodes.values()) {
      send(messageLink, announceMsg, p.address(), p.port());
    }
  }

  private void sendHeartbeats() {
    if (timeOfStep - heartbeatMsg.getTimestamp() < HEARTBEAT_PERIOD) {
      return;
    }
    heartbeatMsg.setTimestamp(timeOfStep);

    for (IMCNode n : nodes.values()) {
      // d("Sending heartbeat to %s", n.getId());
      send(messageLink, heartbeatMsg, n.address(), n.port());
    }
  }

  private void handleIncomingMessages(NetworkLink link) {
    int len;
    while (true) {
      try {
        len = link.recv(rcvBuffer, 0, rcvBuffer.length, 1);
        if (len == 0) {
          break;
        }
        IMCMessage message = IMCDefinition.getInstance().parseMessage(rcvBuffer);
        message.setTimestamp(timeOfStep);

        IMCNode node = nodes.get(message.getSrc());
         d("%d %s %s", message.getSrc(), message.getClass(), node);
        if (node != null) {
          node.handleIncomingMessage(message);
        } 
        else if(message instanceof Announce) {
          handleNewNode((Announce) message);
        } 
        else {
          // d("Ignored message: %d/%s", message.getSrc(), message.getAbbrev());
        }
      } 
      catch (IOException e) {
        e.printStackTrace(System.err);
        return;
      }
    }
  }

  private void handleNewNode(Announce message) {

    switch (message.getSysType()) {
      case CCU:
      case HUMANSENSOR:
      case MOBILESENSOR:
      case STATICSENSOR:
      case WSN:
        return;
      default:
        break;
    }
    d("New vehicle: " + message.getSysName());

    IMCNode vehicle = null;
    for (String serv : message.getServices().split(";")) {
      if (serv.startsWith("imc+udp://")) {
        String s = serv.substring(10);
        s = s.replaceAll("/", " ");
        String[] parts = s.split(":");
        try {
          InetAddress inetAddress = InetAddress.getByName(parts[0]);
          int port = Integer.parseInt(parts[1].split(" ")[0]);  
          d("ALIVE " + inetAddress + ":" + port);
          vehicle = new IMCNode(inetAddress, port, message);
          break;
        } catch (UnknownHostException e) {

        }
      }
    }
    if (vehicle != null) {
      nodes.put(message.getSrc(), vehicle);
    }
  }

  public void send(IMCMessage message, InetAddress address, int port) {
    send(messageLink, message, address, port);
  }

  private void send(NetworkLink link, IMCMessage message, InetAddress address, int port) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      message.serialize(new IMCOutputStream(baos));
      byte[] data = baos.toByteArray();
      link.sendTo(data, 0, data.length, address, port);
    } catch (IOException e) {
      throw new EnvironmentException(e);
    }
  }


  private void setupLinks() {

    for (int port = FIRST_ANNOUNCE_PORT; announceLink == null && port <= LAST_ANNOUNCE_PORT; port++) {
      try {
        MulticastUDPLink link = new MulticastUDPLink(ANNOUNCE_MCAST_ADDR, port);
        link.enable();
        announceLink = link;
      }
      catch (NetworkLinkException e) { 
        if (e.getCause().getClass() != java.net.BindException.class) {
          throw new EnvironmentException(e);
        }
      }
    }
    if (announceLink == null) {
      throw new EnvironmentException("Could not setup announce link");
    }
    for (int port = FIRST_MSG_PORT; messageLink == null && port <= LAST_MSG_PORT; port++) {
      try {
        UDPLink link = new UDPLink(port);
        link.enable();
        messageLink = link;
      }
      catch (NetworkLinkException e) { 
        if (e.getCause().getClass() != java.net.BindException.class) {
          throw new EnvironmentException(e);
        }
      }

    }
    if (messageLink == null) {
      try {
        announceLink.disable();
      } catch (NetworkLinkException e) { }

      throw new EnvironmentException("Could not setup message link");
    }
    d("Links created - multicast %d, message %d", announceLink.getPort(), messageLink.getPort());
  }

  private void teardownLinks() {
    try {
      announceLink.disable();
    } catch (NetworkLinkException e) {
      e.printStackTrace();
    }
  }


  private void setupIdentification() {
    String services = "";
    for (InetAddress itf : NetworkInterfaces.get(false)) {
      services += String.format("imc+udp://%s:%d/;", itf.getHostAddress(), messageLink.getPort());
    }
    services += String.format("imc+info://0.0.0.0/version/%s",
        IMCDefinition.getInstance().getVersion());
    if (services.length() > 0)
      services = services.substring(0, services.length() - 1);
    String sysName = String.format("nvl_%s", UUID.randomUUID().toString());
    int sysAddress = (0b111_00000 << 8) | (sysName.hashCode() & 0x1FFF); 
    announceMsg.setSysType(SystemType.CCU);
    announceMsg.setSysName(sysName);
    announceMsg.setServices(services);
    announceMsg.setSrc(sysAddress);
    announceMsg.setTimestampMillis(0);
    heartbeatMsg.setSrc(sysAddress);
    heartbeatMsg.setTimestampMillis(0);
    d("Announce setup: %s", announceMsg.toString());
  }

  public static void main(String[] args) {
    IMCCommunications comm = getInstance();
    comm.start();
  }


  public NodeSet getConnectedVehicles() {
    return new NodeSet(nodes.values());
  }


}
