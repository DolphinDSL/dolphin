package pt.lsts.nvl.util.wgs84;

/**
 * North-East-Down displacement.
 */
public final class NED {
  /** North displacement */
  public final double north;
  
  /** East displacement */
  public final double east;
  
  /** Down displacement */
  public final double down;

  public NED(double n, double e, double d) {
    north = n;
    east = e;
    down = d;
  }
}


