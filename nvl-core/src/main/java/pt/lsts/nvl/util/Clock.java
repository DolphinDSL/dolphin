package pt.lsts.nvl.util;

public final class Clock {

  public static final long NANOS_PER_MILLIS = 1_000_000L;
  public static final long NANOS_PER_SECOND = 1_000_000_000L;
  public static final double NANOS_TO_SEC_FACTOR = 1e-09;
  
  private static final Clock INSTANCE = new Clock();
  private long ntpDelta;
  private final long wallTimeRef;
  private final long epochOffset;
  private static final int NANO_ADJ_SAMPLES = 100;
  public static final String DEFAULT_NTP_SERVER_ADDRESS = "1.pt.pool.ntp.org";


  private Clock() {
    long d = 0;
    for (int i = 0; i < NANO_ADJ_SAMPLES; i++) {
      d += System.currentTimeMillis()  - System.nanoTime() / NANOS_PER_MILLIS;
    }
    epochOffset = NANOS_PER_MILLIS * (d / NANO_ADJ_SAMPLES);
    wallTimeRef = epochOffset + System.nanoTime();
    ntpDelta = 0L;
  }

  public synchronized static void synchronizeWithNTPServer() {
    synchronizeWithNTPServer(DEFAULT_NTP_SERVER_ADDRESS);
  }
  
  public synchronized static void synchronizeWithNTPServer(String address) {
    INSTANCE.ntpDelta = NTPSync.getDelta(address) * NANOS_PER_MILLIS;
  }

  public static double now()  {
    return epochTimeNano() * NANOS_TO_SEC_FACTOR;
  }

  public static double wallTime() {
    return  ( epochTimeNano() - INSTANCE.wallTimeRef) * NANOS_TO_SEC_FACTOR;
  }
   
  public static long epochTimeNano() { 
    return System.nanoTime() + INSTANCE.epochOffset + INSTANCE.ntpDelta;
  }

  public static long epochTimeMillis() {
    return epochTimeNano() / NANOS_PER_MILLIS;
  }
  
}
