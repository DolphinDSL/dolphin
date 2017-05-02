package pt.lsts.nvl.standalone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.def.SystemType;
import pt.lsts.nvl.net.NetworkLink;
import pt.lsts.nvl.net.MulticastUDPLink;
import pt.lsts.nvl.net.NetworkInterfaces;
import pt.lsts.nvl.net.NetworkLinkException;
import pt.lsts.nvl.net.UDPLink;
import pt.lsts.nvl.runtime.NVLRuntimeException;
import pt.lsts.nvl.util.Clock;
import static pt.lsts.nvl.util.Debug.d;

public class IMCCommunications extends Thread {
  
  static {
    try {
      ANNOUNCE_MCAST_ADDR = InetAddress.getByName("224.0.75.69");
    } catch (UnknownHostException e) {
      throw new NVLRuntimeException(e);
    }
  }

  private static final InetAddress ANNOUNCE_MCAST_ADDR;
  private static final int FIRST_ANNOUNCE_PORT = 30100;
  private static final int LAST_ANNOUNCE_PORT = 30110;
  private static final double ANNOUNCE_PERIOD = 10;
  private static final double HEARTBEAT_PERIOD = 1;
  private static final double CONNECTION_TIMEOUT = 3 * ANNOUNCE_PERIOD;
  
  private final Announce announceMsg = new Announce();
  private final Heartbeat heartbeatMsg = new Heartbeat();
  private final byte[] rcvBuffer = new byte[16384];
  private final Map<Integer,IMCVehicle> vehicles = new HashMap<>();

  private MulticastUDPLink announceLink;
  private UDPLink messageLink;
  private boolean active;
  private double timeOfStep;
  
  private IMCCommunications() {
    super("IMC communications");
    setDaemon(true);
    setupLinks();
    setupIdentification();
    active = true;
  }
  
  public void terminate() {
    active = false;
    teardownLinks();
  }
  
  public void run() {
    while (active) {
      timeOfStep = Clock.now();
      sendAnnounce();
      sendHeartbeats();
      handleIncomingMessages(announceLink);
      handleIncomingMessages(messageLink);
      checkForLostConnections();
    }
  }
  
  private void checkForLostConnections() {
    Iterator<IMCVehicle> itr = vehicles.values().iterator();
    while (itr.hasNext()) {
      IMCVehicle vehicle = itr.next();
      if (vehicle.timeOfLastMessage() - timeOfStep >= CONNECTION_TIMEOUT) {
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
    for (IMCVehicle p : vehicles.values()) {
      send(messageLink, announceMsg, p.address(), p.port());
    }
  }
  
  private void sendHeartbeats() {
    if (timeOfStep - heartbeatMsg.getTimestamp() < HEARTBEAT_PERIOD) {
      return;
    }
    heartbeatMsg.setTimestamp(timeOfStep);
    for (IMCVehicle p : vehicles.values()) {
      send(messageLink, heartbeatMsg, p.address(), p.port());
    }
  }
  
  private void handleIncomingMessages(NetworkLink link) {
    int len;
    try {
      len = link.recv(rcvBuffer, 0, rcvBuffer.length, 1);
      if (len == 0) {
        return;
      }
      IMCMessage message = IMCDefinition.getInstance().parseMessage(rcvBuffer);
      message.setTimestamp(timeOfStep);
      
      d("Incoming message: %s", message.getAbbrev());
      IMCVehicle node = vehicles.get(message.getSrc());
      if (node != null) {
        node.handleIncomingMessage(message);
      } else if(message instanceof Announce) {
        handleNewNode((Announce) message);
      }
    } 
    catch (IOException e) {
      e.printStackTrace(System.err);
      return;
    }
  }
  
  private void handleNewNode(Announce message) {
    d("New node: " + message.getSysName());

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
    IMCVehicle vehicle = null;
    for (String serv : message.getServices().split(";")) {
      if (serv.startsWith("imc+udp://")) {
        String s = serv.substring(10);
        s = s.replaceAll("/", " ");
        String[] parts = s.split(":");
        try {
          InetAddress inetAddress = InetAddress.getByName(parts[0]);
          int port = Integer.parseInt(parts[1].split(" ")[0]);  
          d("ALIVE " + inetAddress + ":" + port);
          vehicle = new IMCVehicle(inetAddress, port, message);
          break;
        } catch (UnknownHostException e) {
          
        }
      }
    }
    if (vehicle != null) {
      vehicles.put(message.getSrc(), vehicle);
    }
  }
  
  public boolean send(String id, IMCMessage message) {
    IMCVehicle vehicle = vehicles.get(id);
    if (vehicle == null) {
      return false;
    }
    send(messageLink, message, vehicle.address(), vehicle.port());
    return true;
  }
  
  private void send(NetworkLink link, IMCMessage message, InetAddress address, int port) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      message.serialize(new IMCOutputStream(baos));
      byte[] data = baos.toByteArray();
      link.sendTo(data, 0, data.length, address, port);
      d("OUT " + address.toString() + ":" + port + " " + message.getAbbrev() );
    } catch (IOException e) {
      throw new NVLRuntimeException(e);
    }
  }
  
  
  private void setupLinks() {
    try {
      messageLink = new UDPLink();
      messageLink.enable();
    } catch (NetworkLinkException e) {
      throw new NVLRuntimeException(e);
    }
    for (int port = FIRST_ANNOUNCE_PORT; announceLink == null && port <= LAST_ANNOUNCE_PORT; port++) {
      try {
        MulticastUDPLink link = new MulticastUDPLink(ANNOUNCE_MCAST_ADDR, port);
        link.enable();
        announceLink = link;
      }
      catch (NetworkLinkException e) { 
        e.printStackTrace();
        
      }
    }
    if (announceLink == null) {
      throw new NVLRuntimeException("Could not setup announce link");
    }
  }
  
  private void teardownLinks() {
    try {
      messageLink.disable();
      announceLink.disable();
    } catch (NetworkLinkException e) {
      e.printStackTrace();
    }
  }


  private void setupIdentification() {
    String services = "";
    for (InetAddress itf : NetworkInterfaces.get(true)) {
      services += String.format("imc+udp://%s:%d/;", itf.getHostAddress(), messageLink.getPort());
    }
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
  }
  
  
 
  
  public static void main(String[] args) {
     new IMCCommunications().start();  
  }
  
}
