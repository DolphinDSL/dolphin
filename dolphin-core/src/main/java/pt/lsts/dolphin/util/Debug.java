package pt.lsts.dolphin.util;

import java.io.PrintStream;

public class Debug {

  public static final String DEBUG_ENV_VAR = "DOLPHIN_DEBUG";
  
  private static boolean debugOn = false;
  private static Log debugLog;
  private static boolean shortVersion = false;
 
  public static void enable(PrintStream ps,boolean shortV) {
    if (!debugOn) {
      debugOn = true;
      shortVersion = shortV;
      debugLog = new Log("Dolphin",shortV);
      debugLog.writeTo(ps);
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
      String fullFmt;
      if(!shortVersion){
        StackTraceElement info = Thread.currentThread().getStackTrace()[3];
	      fullFmt = 
	          String.format("%-16s %d %-16s %s", 
	                        info.getFileName(), info.getLineNumber(),
	                        info.getMethodName(),
	                        format
	                       );
      }
      else {
    	  fullFmt = format;
      }
      debugLog.message(fullFmt, args);
    }
    return true;
  }
}
