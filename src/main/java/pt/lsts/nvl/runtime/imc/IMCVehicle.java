package pt.lsts.nvl.runtime.imc;

import java.net.InetAddress;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.nvl.runtime.NVLVehicle;
import pt.lsts.nvl.runtime.PayloadComponent;
import pt.lsts.nvl.runtime.Position;
import pt.lsts.nvl.runtime.tasks.Task;
import pt.lsts.nvl.util.Variable;

public final class IMCVehicle implements NVLVehicle {

  public interface Subscriber<T extends IMCMessage> {
    void consume(T message);
  }

  private final InetAddress address;
  private final int port;
  private Announce lastAnnounce;
  private Position position;
  private double lastMsgTime;

  private final IdentityHashMap<Class<? extends IMCMessage>, List<Subscriber<IMCMessage>>> 
  subscriptions = new IdentityHashMap<>();
  private Task runningTask;

  IMCVehicle(InetAddress address, int port, Announce a) {
    this.address = address;
    this.port = port;
    consume(a);
    subscribe(Announce.class, this::consume);
    subscribe(EstimatedState.class, this::consume);
  }

  private void consume(Announce message) {
    lastAnnounce = message;
    position = new Position(message.getLat(), message.getLon(), message.getHeight());
  }
  
  private void consume(EstimatedState message) {
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

  public <T extends IMCMessage>
  Variable<T> subscribe(Class<T> classOfMessages) {
    Variable<T> var = new Variable<>();
    subscribe(classOfMessages, msg -> var.set(msg));
    return var;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends IMCMessage>
  void subscribe(Class<T> classOfMessages, Subscriber<T> subscriber) {
    List<Subscriber<IMCMessage>> list = subscriptions.get(classOfMessages);
    if (list == null) { 
      list = new LinkedList<>();
      subscriptions.put(classOfMessages, list);
    }
    list.add((Subscriber<IMCMessage>) subscriber);
  }

  public <T extends IMCMessage>
  void unsubscribe(Class<T> classOfMessages, Subscriber<T> subscriber) {
    List<Subscriber<IMCMessage>> list = subscriptions.get(classOfMessages);
    if (list != null) {
      list.remove(subscriber);
    }
  }

  public void handleIncomingMessage(IMCMessage message) {
    List<Subscriber<IMCMessage>> obsList = subscriptions.get(message.getClass());
    if (obsList != null) {
      for (Subscriber<IMCMessage> obs : obsList) {
        obs.consume(message);
      }
    }
    lastMsgTime = message.getTimestamp();
  }

  public void setRunningTask(Task t) {
    runningTask = t;
  }
  @Override
  public Task getRunningTask() {
    return runningTask;
  }
  
  @Override
  public String getId() {
    return lastAnnounce.getSysName();
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
  public List<PayloadComponent> getPayload() {
    return Collections.emptyList();
  }

  public void send(IMCMessage message) {
    IMCCommunications.getInstance().send(message, address, port);    
  }
}
