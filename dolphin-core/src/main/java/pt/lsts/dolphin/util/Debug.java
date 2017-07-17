package pt.lsts.dolphin.util;

public class Debug {

  private static boolean debugOn = false;
  private static Log debugLog;

  public static void enable() {
    if (!debugOn) {
      debugOn = true;
      debugLog = new Log("Dolphin");
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
      StackTraceElement info = Thread.currentThread().getStackTrace()[3];
      String fullFmt = 
          String.format("%-16s %d %-16s %s", 
                        info.getFileName(), info.getLineNumber(),
                        info.getMethodName(),
                        format
                       );
      debugLog.message(fullFmt, args);
    }
    return true;
  }
}
