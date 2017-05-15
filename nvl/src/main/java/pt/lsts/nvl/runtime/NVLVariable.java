package pt.lsts.nvl.runtime;

public final class NVLVariable<T> {
  private T value;
  private boolean fresh;
  private double updateTime;
  
  public NVLVariable() {
    value = null;
    fresh = false;
    updateTime = 0d;
  }
  
  public synchronized boolean hasFreshValue() {
    return fresh;
  }
  
  public synchronized void set(T newValue, double timestamp) {
    value = newValue;
    fresh = true;
    updateTime = timestamp;
  }
  
  public synchronized T get() {
    fresh = false;
    return value;
  }
  
  public double age(double time) {
    return time - updateTime;
  }
}
