package pt.lsts.dolphin.runtime.mavlink;

import java.io.IOException;

import pt.lsts.dolphin.util.Debug;

public class TestProgram {

  public static void main(String[] args) throws IOException {
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
