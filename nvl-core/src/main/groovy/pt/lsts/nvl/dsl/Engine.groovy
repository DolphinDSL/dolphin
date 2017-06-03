package pt.lsts.nvl.dsl


import java.io.File
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import groovy.util.ObservableList.ElementClearedEvent
import pt.lsts.nvl.runtime.*
import pt.lsts.nvl.runtime.tasks.*
import pt.lsts.nvl.util.Debuggable

/**
 * The engine.
 */
@DSLClass
class Engine implements Debuggable {

  static final def WILDCARD = '_'

  static Engine create(Platform platform) {
    if (instance != null)
      throw new EnvironmentException('Engine already created!')

    instance = new Engine(platform)
    msg 'Engine on !'
    instance
  }

  static Engine getInstance() {
    if (instance == null)
      throw new EnvironmentException('Engine has not been created!')

    instance
  }

  static Environment runtime() {
    return instance.env
  }

  static Platform platform() {
    return instance.env.getPlatform()
  }

  static void msg (String fmt, Object... args) {
    getInstance().d fmt, args
    platform().displayMessage fmt, args
  }

  static void halt(String message='') {
    msg 'Halting program ... %s', message
    throw new Halt(message)
  }

  private static Engine instance

  private Environment env
  private GroovyShell shell
  private SignalSet signalSet
  private boolean runningScript

  private Engine(Platform platform) {
    env = Environment.create platform
    signalSet = new SignalSet()
    runningScript = false
  }

  private void ensureShellIsCreated() {
    if (shell == null) {
      // Imports
      def ic = new ImportCustomizer()
      ic.with {
        addStaticStars 'java.lang.Math'
        addStaticStars 'pt.lsts.nvl.dsl.Instructions'
        addStarImports 'pt.lsts.nvl.dsl'
        addStarImports 'pt.lsts.nvl.runtime'
      }

      // Compiler configuration
      def cfg = new CompilerConfiguration()
      cfg.with {
        addCompilationCustomizers ic
        setTargetBytecode CompilerConfiguration.JDK8
      }
      env.getPlatform().customizeGroovyCompilation(cfg);

      // Define the shell
      shell = new GroovyShell(cfg)
      shell.evaluate 'BootScript.main()'
      for (File script : env.getPlatform().getStartupScripts()) {
        shell.evaluate script
      }
    }
  }

  SignalSet getSignalSet() {
    return signalSet
  }

  boolean isScriptRunning() {
    runningScript
  }
  
  void run(File scriptFile) {
    ensureShellIsCreated()
    synchronized (this) {
      if (runningScript) {
        msg 'Already running a script! Ignoring order ...'
        return
      }
      runningScript = true
    }
    msg 'Running script \'%s\'', scriptFile
    signalSet.clear()
    try {
      shell.evaluate scriptFile
    }
    catch (Halt e) {

    }
    catch (Throwable e) {
      msg 'Unexpected exception ...  %s : %s !',
          e.getClass().getName(), e.getMessage()
      e.printStackTrace(System.out)
    }
    env.releaseAll()
    msg 'Script \'%s\' completed', scriptFile
    synchronized (this) {
      runningScript = false
    }

  }

  void bind(String var, Object value) {
    ensureShellIsCreated()
    shell.setVariable var, value
  }

  Object bindingFor(String var) {
    ensureShellIsCreated()
    shell.getVariable var
  }

  void unbind(String var) {
    ensureShellIsCreated()
    shell.context.variables.remove var
  }

  void run(Task task) {
    env.run task
  }

}