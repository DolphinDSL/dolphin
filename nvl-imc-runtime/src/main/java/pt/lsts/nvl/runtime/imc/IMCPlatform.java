package pt.lsts.nvl.runtime.imc;

import pt.lsts.nvl.runtime.Platform;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import pt.lsts.imc.groovy.dsl.*;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.nvl.runtime.NodeSet;
import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.util.Debuggable;

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
    ic.addStaticStars("pt.lsts.nvl.dsl.imc.Instructions");
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

}
