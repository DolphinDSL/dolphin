package pt.lsts.nvl.standalone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
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

public class AnnounceThread extends Thread {
  
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

  private static class Peer {
    InetAddress address;
    int port;
    double lastUpdate;
    
    Peer(InetAddress address, int port, double time) {
      this.address = address;
      this.port = port;
      this.lastUpdate = time;
    }
  }
  
  private final Announce announceMsg = new Announce();
  private final Heartbeat heartbeatMsg = new Heartbeat();
  private final byte[] rcvBuffer = new byte[16384];
  private final Map<Integer,Peer> peers = new HashMap<>();
  private MulticastUDPLink announceLink;
  private UDPLink messageLink;
  private boolean active;

  public AnnounceThread() {
    setupLinks();
    setupIdentification();
    active = true;
  }
  
  public void terminate() {
    active = false;
    
  }
  
  public void run() {
    while (active) {
      double now = Clock.now();
      sendAnnounce(now);
      sendHeartbeats(now);
      checkForMessages(announceLink);
      checkForMessages(messageLink);
    }
  }
  
  private void sendAnnounce(double now) {
    if (now - announceMsg.getTimestamp() < ANNOUNCE_PERIOD) {
      return;
    }
    announceMsg.setTimestamp(now);
    for (int p = FIRST_ANNOUNCE_PORT; p <= LAST_ANNOUNCE_PORT; p++) {
      sendMessage(announceLink, announceMsg, ANNOUNCE_MCAST_ADDR, p);
     // sendMessage(announceMsg, "255.255.255.255", p);
    }
    for (Peer p : peers.values()) {
      sendMessage(messageLink, announceMsg, p.address, p.port);
    }
  }
  
  private void sendHeartbeats(double now) {
    if (now - heartbeatMsg.getTimestamp() < HEARTBEAT_PERIOD) {
      return;
    }
    heartbeatMsg.setTimestamp(now);
    for (Peer p : peers.values()) {
      sendMessage(messageLink, heartbeatMsg, p.address, p.port);
    }
  }
  
  private void checkForMessages(NetworkLink link) {
    int len;
    try {
      len = link.recv(rcvBuffer, 0, rcvBuffer.length, 1);
      if (len == 0) {
        return;
      }
      IMCMessage msg = IMCDefinition.getInstance().parseMessage(rcvBuffer);
      System.out.println("IN " + msg.getAbbrev());
      
      if (msg instanceof Announce) {
        handleIncomingAnnounce((Announce) msg);
      }
    } 
    catch (IOException e) {
      e.printStackTrace(System.err);
      return;
    }
  }
  private void handleIncomingAnnounce(Announce msg) {
    System.out.println(msg.toString());

    switch (msg.getSysType()) {
      case CCU:
      case HUMANSENSOR:
      case MOBILESENSOR:
      case STATICSENSOR:
      case WSN:
       return;
      default:
        break;
    }

    Peer p = peers.get(msg.getSrc());
    if (p != null) {
      p.lastUpdate = Clock.now();
      return;
    }
    for (String serv : msg.getServices().split(";")) {
      if (serv.startsWith("imc+udp://")) {
        String s = serv.substring(10);
        s = s.replaceAll("/", " ");
        String[] parts = s.split(":");
        try {
          InetAddress inetAddress = InetAddress.getByName(parts[0]);
          int port = Integer.parseInt(parts[1].split(" ")[0]);  
          System.out.println("ALIVE " + inetAddress + ":" + port);
          p = new Peer(inetAddress, port, Clock.now());
          break;
        } catch (UnknownHostException e) {
          
        }
      }
    }
    if (p != null) {
      peers.put(msg.getSrc(), p);
    }
    
  }
  
  public void sendMessage(NetworkLink link, IMCMessage msg, String peer, int port) {
    try {
      sendMessage(link, msg,  InetAddress.getByName(peer), port);
    } catch (UnknownHostException e) {
      throw new NVLRuntimeException(e);
    }
  }

  public void sendMessage(NetworkLink link, IMCMessage msg, InetAddress address, int port) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      msg.serialize(new IMCOutputStream(baos));
      byte[] data = baos.toByteArray();
      link.sendTo(data, 0, data.length, address, port);
      System.out.println("OUT " + address.toString() + ":" + port + " " + msg.getAbbrev() );

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
     new AnnounceThread().start();  
  }
  
}
