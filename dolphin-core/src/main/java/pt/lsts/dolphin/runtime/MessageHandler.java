package pt.lsts.dolphin.runtime;

import java.util.IdentityHashMap;

public final class MessageHandler<NodeType extends Node, MessageType> {
  
  public interface Consumer<N,M> {
    void consume(N node, M message);
  }
  
  private final IdentityHashMap<Class<? extends MessageType>,
                                Consumer<NodeType,? extends MessageType>> 
    handlers = new IdentityHashMap<>();
  
  public <T extends MessageType> void bind(Class<T> clazz, Consumer<NodeType,T> consumer) {
    handlers.put(clazz, consumer);
  }
  
  public <T extends MessageType> void unbind(Class<T> clazz) {
    handlers.remove(clazz);
  }
  
  public void process(NodeType node, MessageType message) {
    @SuppressWarnings("unchecked")
    Consumer<NodeType,MessageType> consumer 
       = (Consumer<NodeType,MessageType>) handlers.get(message.getClass());
    if (consumer != null) {
      consumer.consume(node, message);
    }
  }
}
