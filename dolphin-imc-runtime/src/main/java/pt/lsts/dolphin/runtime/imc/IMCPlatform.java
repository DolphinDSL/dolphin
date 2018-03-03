package pt.lsts.dolphin.runtime.imc;

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
import pt.lsts.imc.IMCDefinition;

public final class IMCPlatform implements Platform, Debuggable {

  private final IMCCommunications comm = IMCCommunications.getInstance();
  
  public IMCPlatform() {
    if (!comm.isAlive()) {
      comm.start();
    }
  }
  
  @Override
  public PlatformTask getPlatformTask(String id) {
    return new IMCPlanTask(id);
  }

  @Override
  public NodeSet getConnectedNodes() {
    return comm.getConnectedVehicles();
  }

  @Override
  public void displayMessage(String format, Object... args) {
     System.out.printf(format, args);
     System.out.println();
    
  }

  @Override
  public void customizeGroovyCompilation(CompilerConfiguration cc) {
    d("Customizing compilation for IMC runtime ...");
    ImportCustomizer ic = new ImportCustomizer();
    ic.addStaticStars("pt.lsts.dolphin.dsl.imc.Instructions");
    ic.addStarImports("pt.lsts.imc.groovy.dsl");
    for (String msg : IMCDefinition.getInstance().getConcreteMessages()) {
      ic.addImports("pt.lsts.imc." + msg);
    }
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
