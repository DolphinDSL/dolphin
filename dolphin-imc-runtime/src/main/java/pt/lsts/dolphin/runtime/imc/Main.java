package pt.lsts.dolphin.runtime.imc;

import java.io.File;

import pt.lsts.dolphin.dsl.Engine;

public final class Main {

  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Please specify the script(s) to run!");
      return;
    }

    pt.lsts.dolphin.util.Debug.enable(System.err,false);
    IMCCommunications.getInstance().start();
    try {
      Engine engine = Engine.create(new IMCPlatform());

      for (String fileName : args) {
        engine.run(new File(fileName));
      }
    }
    finally {
      IMCCommunications.getInstance().terminate();
    }
  }

  private Main() {

  }
}
