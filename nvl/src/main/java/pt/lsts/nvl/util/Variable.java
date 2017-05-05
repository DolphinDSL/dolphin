package pt.lsts.nvl.util;

public final class Variable<T> {
  private T value;
  private boolean fresh;

  public Variable() {
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
