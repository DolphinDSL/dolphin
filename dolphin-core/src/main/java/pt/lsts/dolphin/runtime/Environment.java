package pt.lsts.dolphin.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pt.lsts.dolphin.runtime.tasks.Task;
import pt.lsts.dolphin.runtime.tasks.TaskExecutor;
import pt.lsts.dolphin.util.Clock;
import pt.lsts.dolphin.util.Debuggable;

public final class Environment implements Debuggable {

  private static Environment INSTANCE;

  public static Environment create(Platform platform) {
    if (INSTANCE != null) {
      throw new EnvironmentException("Runtime has already been created");
    } 
    INSTANCE = new Environment(platform);
    return INSTANCE;
  }

  public static Environment getInstance() {
    if (INSTANCE == null) {
      throw new EnvironmentException("Runtime has not been created");
    }
    return INSTANCE;
  }

  private final Platform platform;
  private final NodeSet boundVehicles;
  
  private Environment(Platform p) {
    platform = p;
    boundVehicles = new NodeSet();
  }



  public Platform getPlatform() {
    return platform;
  }


  private double gDefaultConnectionTimeout = Node.INITIAL_CONNECTION_TIMEOUT_SETTING;
  
  public void setDefaultConnectionTimeout(double timeout) {
    Node.assertValidTimeout(timeout);
    gDefaultConnectionTimeout = timeout;
  }
  
  
  public double getDefaultConnectionTimeout() {
    return gDefaultConnectionTimeout;
  }
  public void run(Task task) {
    
    Map<Task,List<Node>> allocation = new HashMap<>();
    
    if (task.allocate(boundVehicles.clone(), allocation) == false) {
      throw new EnvironmentException("No vehicles to run task!");
    }
    
    TaskExecutor executor = task.getExecutor();
    executor.initialize(allocation);
    executor.start();
    while (executor.getState() != TaskExecutor.State.COMPLETED) {
      executor.step();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new EnvironmentException(e);//Halt("Program interrupted!");
      }
    }
  }

  

  public NodeSet select(List<NodeFilter> reqList) {

    NodeSet available = platform.getConnectedNodes();
    available.removeAll(boundVehicles);
    
    platform.displayMessage("Available nodes: %d", available.size());

    for (Node v : available) {
      platform.displayMessage("  id=%s type=%s", v.getId(), v.getType());
    }

    NodeSet set = new NodeSet();
    for (NodeFilter req : reqList) {
      platform.displayMessage("Matching requirement: %s", req.toString());
      Optional<Node> ov = 
          available.stream()
          .filter(v -> !set.contains(v) && req.matchedBy(v))
          .findFirst();
      if (! ov.isPresent()) {
        platform.displayMessage("Requirements were not met!");
        return NodeSet.EMPTY;
      }
      platform.displayMessage("Requirement met by node %s", ov.get().getId());
      set.add(ov.get());
    }
    platform.displayMessage("Selected nodes: %s", set);
    for (Node n : set) {
      n.setConnectionTimeout(getDefaultConnectionTimeout());
    }
    boundVehicles.addAll(set);
    return set;
  }

  public NodeSet select(List<NodeFilter> reqList, double timeout) {
    double startTime = Clock.now();
    d("Performing selection with timeout %f", timeout);
    double delayTime = Math.max(1.0,  timeout * 0.1);
    NodeSet set = NodeSet.EMPTY;

    while (true) {
      set = select(reqList);
      if (set != NodeSet.EMPTY || Clock.now() - startTime >= timeout) {
        break;
      }
      pause(delayTime);
    } 
    return set;
  }
  
  public void release(NodeSet set) {
    boundVehicles.removeAll(set);
  }
  
  public void releaseAll() {
    d("Releasing bound vehicles: %s", boundVehicles);
    for (Node n : boundVehicles) {
      d("Releasing %s", n.getId());
      n.release();
    }
    boundVehicles.clear();
  }
  
  public void pause(double time) {
    try {
      Thread.sleep(Math.round(time * 1e+03));
    }
    catch (InterruptedException e) {
      throw new EnvironmentException(e);
    }
  }
}

