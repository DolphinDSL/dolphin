package pt.lsts.dolphin.runtime.mavlink;

import java.io.File;

import pt.lsts.dolphin.dsl.Engine;

/**
 * Program to interpret Dolphin files with the MAVLink runtime.
 *
 */
public final class Main {

  /**
   * Main method.
   * @param args Program arguments.
   */
  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Please specify the script(s) to run!");
      return;
    }

    pt.lsts.dolphin.util.Debug.enable(System.err,false);
    MAVLinkCommunications.getInstance().start();
    try {
      Engine engine = Engine.create(new MAVLinkPlatform());
      for (String fileName : args) {
        engine.run(new File(fileName));
      }
    }
    finally {
      MAVLinkCommunications.getInstance().terminate();
    }
  }

  /**
   * Prevent instantiation.
   */
  private Main() {

  }
}
