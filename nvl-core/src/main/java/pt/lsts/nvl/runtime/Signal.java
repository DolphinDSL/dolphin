package pt.lsts.nvl.runtime;

public final class Signal {

  private boolean fired = false;
  
  public void fire() {
    fired = true;
  }
  
  public boolean test() {
    return fired;
  }
  
  public boolean testAndClear() {
    boolean b = fired;
    if (b) {
      fired = false;
    }
    return b;
  }
}
