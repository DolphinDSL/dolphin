package pt.lsts.dolphin.runtime.mavlink;


import pt.lsts.dolphin.runtime.Position;
import pt.lsts.dolphin.util.Debug;
import pt.lsts.dolphin.util.wgs84.NED;
import pt.lsts.dolphin.util.wgs84.WGS84;

/**
 * Temporary test program for sanity checks.
 *
 */
public class TestProgram {

  /**
   * Main method.
   * @param args Arguments (ignored).
   */
  public static void main(String[] args) {
    MAVLinkCommunications comm = MAVLinkCommunications.getInstance();
    Debug.enable(System.out, false);
    comm.start();

    while(true) {
//      for (MAVLinkNode n : comm.getNodes()) {
//        n.getDownloadProtocol().start();
//      }
      try { 
        Thread.sleep(10000);
      } 
      catch (InterruptedException e) {
        
      }
      for (MAVLinkNode n : comm.getNodes()) {
        Position ref = Position.fromDegrees(41.185781, -8.70606486, 0.0);
        NED ned = new NED(100, 0, 0);
        Position a = WGS84.displace(ref, ned);
        NED ned2 = new NED(100, 200, 0);
        Position b = WGS84.displace(a, ned2);
        
        Position[] wpts = { ref, a, b };
            
        n.getUploadProtocol().start(wpts);
      }
    }

  }
}
