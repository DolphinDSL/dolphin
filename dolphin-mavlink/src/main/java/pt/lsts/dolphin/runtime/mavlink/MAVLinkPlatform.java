package pt.lsts.dolphin.runtime.mavlink;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import pt.lsts.dolphin.runtime.NodeSet;
import pt.lsts.dolphin.runtime.Platform;
import pt.lsts.dolphin.runtime.tasks.PlatformTask;
import pt.lsts.dolphin.util.Debuggable;

public final class MAVLinkPlatform implements Platform, Debuggable {

  private final MAVLinkCommunications comm = MAVLinkCommunications.getInstance();
  
  public MAVLinkPlatform() {
    if (!comm.isAlive()) {
      comm.start();
    }
  }
  
  @Override
  public PlatformTask getPlatformTask(String id) {
    throw new RuntimeException("Not implemented yet!");
  }

  @Override
  public NodeSet getConnectedNodes() {
    return new NodeSet(comm.getNodes());
  }

  @Override
  public void displayMessage(String format, Object... args) {
     System.out.printf(format, args);
     System.out.println();
    
  }

  @Override
  public void customizeGroovyCompilation(CompilerConfiguration cc) {
    d("Customizing compilation for MAVlink runtime ...");
    ImportCustomizer ic = new ImportCustomizer();
    ic.addStaticStars("pt.lsts.dolphin.dsl.mavlink.Instructions");
    ic.addStarImports("pt.lsts.mavlink.groovy.dsl");
    ic.addStarImports("com.MAVLink.Messages");
    ic.addStarImports("com.MAVLink.common");
    ic.addStarImports("com.MAVLink.enums");
    cc.addCompilationCustomizers(ic);
  }

  @Override
  public List<File> getExtensionFiles() {
    return Collections.emptyList();
  }

  @Override
  public String askForInput(String prompt) {
    System.out.println(prompt);
    try (Scanner in = new Scanner(System.in)) {
      return in.nextLine();
    }
  }

}
