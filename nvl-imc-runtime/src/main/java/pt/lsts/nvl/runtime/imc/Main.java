package pt.lsts.nvl.runtime.imc;

import java.io.File;

import pt.lsts.nvl.dsl.NVLEngine;

public final class Main {

  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Please specify the script(s) to run!");
      return;
    }
    pt.lsts.nvl.util.Debug.enable();
    NVLEngine engine = NVLEngine.create(new IMCPlatform());

    for (String fileName : args) {
      engine.run(new File(fileName));
    }
  }

  private Main() {

  }
}
