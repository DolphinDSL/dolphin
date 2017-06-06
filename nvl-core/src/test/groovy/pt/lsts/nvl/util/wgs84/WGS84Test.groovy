package pt.lsts.nvl.util.wgs84

import pt.lsts.nvl.runtime.Position
import static pt.lsts.nvl.util.wgs84.WGS84.*

import spock.lang.Specification

class WGS84Test extends Specification {
  
  static final Position APDL = Position.fromDegrees(41.185781, -8.70606486, 0.0);
  static final Position APDL_B1 = Position.fromDegrees(41.184742, -8.704601, 0.0);
  static final Position APDL_B2 = Position.fromDegrees(41.186057, -8.706045, 0.0);
  
  static final double LLH_TOL = 1e-06;
  static final double NED_TOL = 1e-01;

  def 'To ECEF'() {
    when:
      def actual = WGS84.toECEF(pos)
    then:
      Math.abs(actual.x - expected.x) <= LLH_TOL
      Math.abs(actual.y - expected.y) <= LLH_TOL
      Math.abs(actual.z - expected.z) <= LLH_TOL
      
    where:
      pos << APDL
      expected << new ECEF(4751642.734626,-727618.268373,4177972.426444)
  }
  
  def 'From ECEF'() {
    when:
      def pos2 = WGS84.toECEF(pos).toWGS84()
    then:
      Math.abs(pos.lat - pos2.lat) <= LLH_TOL
      Math.abs(pos.lon - pos2.lon) <= LLH_TOL
      Math.abs(pos.hae - pos2.hae) <= LLH_TOL
      
    where:
      pos << [APDL, APDL_B1, APDL_B2]
  }
  
  def 'Distance' () {
    when:
      def actual = a.distanceTo(b)
    then:
      Math.abs(actual - expected) <= NED_TOL
    where:
      a <<         [APDL,       APDL_B1,    APDL_B2,    APDL]
      b <<         [APDL_B1,    APDL_B2,    APDL,       APDL]
      expected <<  [168.518497, 189.803976, 30.697125,  0.0]
  }
  
  def 'Displacement' () {
    when:
      NED ned = WGS84.displacement(a,b)
    then:
      Math.abs(ned.north - n) <= NED_TOL
      Math.abs(ned.east - e) <= NED_TOL
      Math.abs(ned.down) <= NED_TOL
    where:
      a <<         [APDL,       APDL_B1,    APDL_B2,    APDL]
      b <<         [APDL_B1,    APDL_B2,    APDL,       APDL]
      n <<         [-115.3877,  146.04161,  -30.651871,  0]
      e <<         [122.8176,   -121.232823, -1.666217,  0] 
  }
  
  def 'Displace' () {
    when:
      Position pos2 = WGS84.displace(ref, new NED(n, e, 0))
    then:
      Math.abs(pos.lat - pos2.lat) <= LLH_TOL
      Math.abs(pos.lon - pos2.lon) <= LLH_TOL
    where:
      ref <<         [APDL,       APDL_B1,    APDL_B2,    APDL]
      n <<         [-115.3877,  146.04161,  -30.651871,  0]
      e <<         [122.8176,   -121.232823, -1.666217,  0]
      pos <<         [APDL_B1,    APDL_B2,    APDL,       APDL]
  }
  
}
