package pt.lsts.dolphin.runtime;


import java.io.File;
import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;

import pt.lsts.dolphin.runtime.tasks.PlatformTask;
import pt.lsts.dolphin.util.Debuggable;

public interface Platform extends Debuggable {
  
  NodeSet getConnectedNodes();
  
  PlatformTask getPlatformTask(String id);
  
  void displayMessage(String format, Object... args);
  
  void customizeGroovyCompilation(CompilerConfiguration cc);
  
  List<File> getExtensionFiles();
  
  String askForInput(String prompt);
}
