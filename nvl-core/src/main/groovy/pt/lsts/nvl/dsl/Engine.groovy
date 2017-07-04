package pt.lsts.nvl.dsl


import java.io.File
import java.util.regex.Pattern.Start

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

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
  private Thread executionThread

  private Engine(Platform platform) {
    env = Environment.create platform
    signalSet = new SignalSet()
    runningScript = false
  }

  private void ensureShellIsCreated() {
    if (shell == null) {
      try {
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
        msg('Customising compiler for platform ...');
        env.getPlatform().customizeGroovyCompilation(cfg);

        // Define the shell
        shell = new GroovyShell(cfg)

        for (File script : env.getPlatform().getExtensionFiles()) {
          //d 'Extension file: %s', script
          msg("- Parsing extension script '%s'", script)
          try {
            shell.evaluate script
          }
          catch(Exception e) {
            msg('- There were errors: %s - %s', e.getClass(), e.getMessage());
          }
        }
        shell.evaluate 'BootScript.main()'
      }
      catch (Exception e) {
        msg('Some errors creating Groovy shell - %s %s',
            e.getClass().getName(), e.getMessage());
        e.printStackTrace(System.err);
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
    shell.context.variables.clear()
    
    executionThread = new Thread() {
          @Override
          public void run() {
            try {
              shell.evaluate scriptFile
            }
            catch (InterruptedException  e) {
              msg 'Script interrupted!'
            }
            catch (Halt e) {

            }
            catch (Throwable e) {
              msg 'Unexpected exception ...  %s : %s !',
                  e.getClass().getName(), e.getMessage()
              e.printStackTrace(System.err)
            }
            env.releaseAll()
            msg 'Script \'%s\' completed', scriptFile
            synchronized (this) {
              runningScript = false
            }
          }
        };
    executionThread.start();
  }

  void stopExecution() {
    synchronized (this) {
      if (! runningScript) {
        msg 'No script is running!'
        executionThread = null
        return
      }
      msg 'Stopping script ...'
      for (int i = 0; i < 10; i++) {
        if (!executionThread.isAlive()) {
          break;
        }
        executionThread.interrupt();
        env.pause 0.01
      }
      for (int i = 0; i < 10; i++) {
        if (!executionThread.isAlive()) {
          break;
        }
        executionThread.stop()
      }
      env.releaseAll()
      runningScript = false
      msg 'Script stopped ...'
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
  
  def ask(String prompt) {
    String input = env.getPlatform().askForInput prompt
    shell.evaluate input
  }
}