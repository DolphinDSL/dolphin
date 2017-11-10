package pt.lsts.dolphin.util;

import java.io.PrintStream;

public class Debug {

  private static boolean debugOn = false;
  private static Log debugLog;
  private static boolean shortVersion = false;

  public static void enable(PrintStream ps,boolean time) {
    if (!debugOn) {
      debugOn = true;
      shortVersion = time;
      debugLog = new Log("Dolphin",time);
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
      StackTraceElement info = Thread.currentThread().getStackTrace()[3];
      String fullFmt;
      if(!shortVersion){
	      fullFmt = 
	          String.format("%-16s %d %-16s %s", 
	                        info.getFileName(), info.getLineNumber(),
	                        info.getMethodName(),
	                        format
	                       );
      }
      else {
    	  fullFmt = String.format("%s", format);
      }
      debugLog.message(fullFmt, args);
    }
    return true;
  }
}
