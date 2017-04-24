package pt.lsts.nvl.util.wgs84;

/**
 * WGS-84 coordinate.
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
   * @param lat Latitude (in radians).
   * @param lon Longitude (in radians).
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

    double ox = c2.x - c1.x;
    double oy = c2.y - c2.y;
    double oz = c2.z - c2.z;
    double slat = Math.sin(lat);
    double clat = Math.cos(lat);
    double slon = Math.sin(lon);
    double clon = Math.cos(lon);

    double n = - slat * clon * ox - slat * slon * oy + clat  * oz; // North 
    double e = - slon * ox + clon * oy; // East
    return new NED(n, e, other.hae - hae);
  }

  /**
   * Displace coordinated by NED offset.
   * @param ned NED offset.
   * @return Displaced coordinate.
   */
  public WGS84 displace(NED ned)
  {
    // Convert reference to ECEF coordinates
    ECEF ecef = toECEF();

    // Compute Geocentric latitude
    double phi = Math.atan2(ecef.z, Math.sqrt(ecef.x*ecef.x + ecef.y*ecef.y));

    // Compute all needed sine and cosine terms for conversion.
    double slon = Math.sin(lon);
    double clon = Math.cos(lon);
    double sphi = Math.sin(phi); 
    double cphi = Math.cos(phi);

    // Obtain ECEF coordinates of displaced point
    // Note: some signs from standard ENU formula 
    // are inverted - we are working with NED (= END) coordinates 

    double dx = ecef.x - slon * ned.east - clon * sphi * ned.north - clon * cphi * ned.down;
    double dy = ecef.y + clon * ned.east - slon * sphi * ned.north - slon * cphi * ned.down;
    double dz = ecef.z                   + cphi * ned.north        - sphi * ned.down;

    // Convert back to WGS-84 coordinates 
    return new ECEF(dx,dy,dz).toWGS84();
  }


  /**
   *  Convert WGS-84 coordinates to ECEF (Earch Center Earth Fixed) coordinates.
   * @param c WGS-84 coordinate.
   * @return Corresponding ECEF coordinate.
   */ 
  public ECEF toECEF() {
    double cos_lat = Math.cos(lat);
    double sin_lat = Math.sin(lat);
    double cos_lon = Math.cos(lon);
    double sin_lon = Math.sin(lon);
    double rn = SMA / Math.sqrt(1.0 - ECC_SQ * sin_lat * sin_lat);
    double x = (rn + hae) * cos_lat * cos_lon;
    double y = (rn + hae) * cos_lat * sin_lon;
    double z = (((1.0 - ECC_SQ) * rn) + hae) * sin_lat;
    return new ECEF(x, y, z);
  }
  
  static double
  n_rad(double lat)
  {
    double lat_sin = Math.sin(lat);
    return SMA / Math.sqrt(1 - ECC_SQ * (lat_sin * lat_sin));
  }
}


