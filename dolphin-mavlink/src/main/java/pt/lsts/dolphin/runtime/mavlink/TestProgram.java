package pt.lsts.dolphin.runtime.mavlink;


import pt.lsts.dolphin.util.Debug;

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
      for (MAVLinkNode n : comm.getNodes()) {
        n.getDownloadProtocol().start();
      }
      try { 
        Thread.sleep(10000);
      } 
      catch (InterruptedException e) {
        
      }
    }

  }
}
