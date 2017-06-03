package pt.lsts.nvl.runtime;


import pt.lsts.nvl.runtime.tasks.PlatformTask;
import pt.lsts.nvl.util.Debuggable;

import java.io.File;
import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;

public interface Platform extends Debuggable {
  
  NodeSet getConnectedNodes();
  
  PlatformTask getPlatformTask(String id);
  
  void displayMessage(String format, Object... args);
  
  void customizeGroovyCompilation(CompilerConfiguration cc);
  
  List<File> getStartupScripts();
}
