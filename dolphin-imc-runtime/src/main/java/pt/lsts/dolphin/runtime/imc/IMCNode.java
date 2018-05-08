package pt.lsts.dolphin.runtime.imc;

import java.net.InetAddress;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import pt.lsts.dolphin.runtime.AbstractNode;
import pt.lsts.dolphin.runtime.Payload;
import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.util.Clock;
import pt.lsts.dolphin.util.Debuggable;
import pt.lsts.dolphin.util.Variable;
import pt.lsts.imc.Abort;
import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;

public final class IMCNode extends AbstractNode implements Debuggable {

  private final InetAddress address;
  private final int port;
  private Announce lastAnnounce;
  private Position position;
  private double lastMsgTime;

  IMCNode(InetAddress address, int port, Announce a) {
    super(a.getSysName());
    this.address = address;
    this.port = port;
    consume(a);
  }

  void consume(Announce message) {
    lastAnnounce = message;
    position = new Position(message.getLat(), message.getLon(), message.getHeight());
  }
  
  void consume(EstimatedState message) {
    position = new Position(message.getLat(), message.getLon(), message.getHeight());
  }

  public double timeOfLastMessage() {
    return lastMsgTime;
  }

  public InetAddress address() {
    return address;
  }

  public int port() {
    return port;
  }

  public void send(IMCMessage message) {
    d("OUT: %s %s", getId(), message.getAbbrev());
    IMCCommunications.getInstance().send(message, address, port);    
  }
  
  @Override
  public String getType() {
    return lastAnnounce.getSysType().toString();
  }


  @Override
  public Position getPosition() {
    return position;
  }

  @Override
  public Payload getPayload() {
    return new Payload(Collections.emptyList());
  }
  
  @Override
  public String toString() {
    return getId();
  }

  @Override
  public void release() {
    // Temporary workaround
    send(new Abort()); 
  }

}
