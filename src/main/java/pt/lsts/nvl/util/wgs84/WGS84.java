package pt.lsts.nvl.util.wgs84;

import static java.lang.Math.*;

/**
 * WGS-84 coordinate.
 * @author Eduardo Marques, Ricardo Martins (original DUNE code)
 * @author Eduardo Marques (port)
 */
public final class WGS84 {

  /** Semi-major axis. **/
  public static final double SMA = 6378137.0;

  /** First eccentricity squared. **/
  public static final double ECC_SQ = 0.00669437999013;

  /** WGS84 latitude. */
  public final double lat; 

  /** WGS84 longitude. */
  public final double lon;

  /** WGS84 height. */
  public final double hae;

  /**
   * Constructor
   * @param lat Latitude (radians).
   * @param lon Longitude (radians).
   * @param hae Height (meters)
   */
  public WGS84(double lat, double lon, double hae) {
    this.lat = lat;
    this.lon = lon;
    this.hae = hae;
  }

  /**
   * Calculate distance between two WGS-84 coordinates.
   * @param a First coordinate.
   * @param b Second coordinate.
   * @return Distance between coordinates.
   */
  public double distanceTo(WGS84 other) {
    return toECEF().distanceTo(other.toECEF());
  }

  /**
   * Get NED offset from this coordinate to a given coordinate.
   * @param other 
   * @return
   */
  public NED displacementTo(WGS84 other) {
    ECEF c1 = toECEF(); 
    ECEF c2 = other.toECEF(); 

    double dx = c2.x - c1.x;
    double dy = c2.y - c2.y;
    double dz = c2.z - c2.z;
    double slat = sin(lat);
    double clat = cos(lat);
    double slon = sin(lon);
    double clon = cos(lon);
    double n = -slat * clon * dx - slat * slon * dy + clat * dz; 
    double e = -slon * dx + clon * dy; 
    double d = -clat * clon * dx - clat * slon * dy - slat * dz;
    return new NED(n, e, d);
  }

  /**
   * Displace coordinated by NED offset.
   * @param ned NED offset.
   * @return Displaced coordinate.
   */
  public WGS84 displace(NED ned) {
    // Convert reference to ECEF coordinates
    ECEF ecef = toECEF();

    // Compute Geocentric latitude
    double phi = atan2(ecef.z, sqrt(ecef.x*ecef.x + ecef.y*ecef.y));

    // Compute all needed sine and cosine terms for conversion.
    double slon = sin(lon);
    double clon = cos(lon);
    double sphi = sin(phi); 
    double cphi = cos(phi);

    // Obtain ECEF coordinates of displaced point
    // Note: some signs from standard ENU formula 
    // are inverted - we are working with NED (= END) coordinates 

    double dx = ecef.x - slon * ned.east - clon * sphi * ned.north - clon * cphi * ned.down;
    double dy = ecef.y + clon * ned.east - slon * sphi * ned.north - slon * cphi * ned.down;
    double dz = ecef.z                   + cphi * ned.north        - sphi * ned.down;

    // Convert back to WGS-84 coordinates 
    return new ECEF(dx, dy, dz).toWGS84();
  }


  /**
   *  Convert WGS-84 coordinates to ECEF (Earch Center Earth Fixed) coordinates.
   * @param c WGS-84 coordinate.
   * @return Corresponding ECEF coordinate.
   */ 
  public ECEF toECEF() {
    double cos_lat = cos(lat);
    double sin_lat = sin(lat);
    double cos_lon = cos(lon);
    double sin_lon = sin(lon);
    double rn = SMA / sqrt(1.0 - ECC_SQ * sin_lat * sin_lat);
    double x = (rn + hae) * cos_lat * cos_lon;
    double y = (rn + hae) * cos_lat * sin_lon;
    double z = (((1.0 - ECC_SQ) * rn) + hae) * sin_lat;
    return new ECEF(x, y, z);
  }
  
  /** Compute the radius of curvature in the prime vertical (Rn).
   * 
   * @param lat Latitude.
   * @return radius of curvature in the prime vertical (radians).
   */
  static double
  computeRn(double lat)
  {
    double lat_sin = sin(lat);
    return SMA / sqrt(1 - ECC_SQ * (lat_sin * lat_sin));
  }
}


