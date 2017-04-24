package pt.lsts.nvl.util.wgs84;

/**
 * Earth-Center Earth-Fixed (ECEF) coordinate.
 *
 */
public class ECEF {
  public final double x, y, z;

  public ECEF(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  
  public WGS84 toWGS84() {
    double p = Math.sqrt(x * x + y * y);
    double lon = Math.atan2(y, x);
    double lat = Math.atan2(z / p, 0.01);
    double n = WGS84.n_rad(lat);
    double hae = p / Math.cos(lat) - n;
    double old_hae = -1e-9;
    double num = z / p;

    while (Math.abs(hae - old_hae) > 1e-4) 
    {
      old_hae = hae;
      double den = 1 - WGS84.ECC_SQ * n / (n + hae);
      lat = Math.atan2(num, den);
      n = WGS84.n_rad(lat);
      hae = p / Math.cos(lat) - n;
    }
    return new WGS84(lat, lon, hae);
  }
  
  public double distanceTo(ECEF other) {
    double dx = x - other.x;
    double dy = y - other.y;
    double dz = z - other.z;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

}

