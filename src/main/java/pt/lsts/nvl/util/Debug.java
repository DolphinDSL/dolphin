package pt.lsts.nvl.util;

public class Debug {

  private static boolean debugOn = false;
  private static Log debugLog;

  public static void enable() {
    if (!debugOn) {
      debugOn = true;
      debugLog = new Log("D");
      debugLog.writeTo(System.err);
    }
  }

  public static void disable() {
    if (debugOn) {
      debugOn = false;
      debugLog = null;
    }
  }

  public static boolean d(String format, Object... args) {
    if (debugOn) {
      StackTraceElement info = Thread.currentThread().getStackTrace()[2];
      String fullFmt = 
          String.format("%s()@%s:%d|%s", 
                        info.getMethodName(),
                        info.getFileName(), info.getLineNumber(),
                        format
                       );
      debugLog.message(fullFmt, args);
    }
    return true;
  }
}
