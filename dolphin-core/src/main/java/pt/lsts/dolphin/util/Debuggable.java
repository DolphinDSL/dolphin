package pt.lsts.dolphin.util;

public interface Debuggable {
  default boolean d(String format, Object... args) {
    return Debug.d(format, args);
  }
}
