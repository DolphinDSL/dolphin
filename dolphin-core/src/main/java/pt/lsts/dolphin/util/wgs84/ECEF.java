package pt.lsts.dolphin.util.wgs84;

import static java.lang.Math.*;

import pt.lsts.dolphin.runtime.Position;

/**
 * Earth-Center Earth-Fixed (ECEF) coordinates.
 * 
 * @author Eduardo Marques, Ricardo Martins (original DUNE code)
 * @author Eduardo Marques (port)
 */
public strictfp final class ECEF {
  public final double x, y, z;

  /**
   * Constructor.
   * @param x X coordinate.
   * @param y Y coordinate.
   * @param z Z coordinate.
   */
  public ECEF(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  /**
   * Convert to WGS84 coordinate.
   * @return Corresponding WGS84 coordinate.
   */
  public Position toWGS84() {
    double p = sqrt(x * x + y * y);
    double lon = atan2(y, x);
    double lat = atan2(z / p, 0.01);
    double n = WGS84.computeRn(lat);
    double hae = p / cos(lat) - n;
    double old_hae = -1e-9;
    double num = z / p;

    while (abs(hae - old_hae) > 1e-4) 
    {
      old_hae = hae;
      double den = 1 - WGS84.ECC_SQ * n / (n + hae);
      lat = atan2(num, den);
      n = WGS84.computeRn(lat);
      hae = p / cos(lat) - n;
    }
    return new Position(lat, lon, hae);
  }
  
  public double distanceTo(ECEF other) {
    double dx = x - other.x;
    double dy = y - other.y;
    double dz = z - other.z;
    return sqrt(dx * dx + dy * dy + dz * dz);
  }
  
  @Override
  public String toString(){
    return String.format("ECEF(%f, %f, %f)", x, y, z);
  }
}

