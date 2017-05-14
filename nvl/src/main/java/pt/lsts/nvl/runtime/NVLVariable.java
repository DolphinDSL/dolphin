package pt.lsts.nvl.runtime;

public final class NVLVariable<T> {
  private T value;
  private boolean fresh;

  public NVLVariable() {
    value = null;
    fresh = false;
  }
  public boolean hasFreshValue() {
    return fresh;
  }
  public void set(T newValue) {
    value = newValue;
    fresh = true;
  }
  public T get() {
    return value;
  }
}
