package pt.lsts.nvl.util.wgs84;

/**
 * North-East-Down displacement.
 */
public class NED {
  public final double north, east, down;

  NED(double n, double e, double d) {
    north = n;
    east = e;
    down = d;
  }
}


