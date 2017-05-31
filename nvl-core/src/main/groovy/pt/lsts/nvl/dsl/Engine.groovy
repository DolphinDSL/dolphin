package pt.lsts.nvl.dsl


import java.io.File
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import pt.lsts.nvl.runtime.*
import pt.lsts.nvl.runtime.tasks.*
import pt.lsts.nvl.util.Debuggable

/**
 * The NVL engine.
 */
@DSLClass
class Engine implements Debuggable {
  
  static final def WILDCARD = '_'
  
  static Engine create(Platform platform) {
    if (instance != null)
      throw new ExecutionException('Engine already created!')
      
    instance = new Engine(platform)
    msg 'Engine on !'
    instance
  }
  
  static Engine getInstance() {
    if (instance == null)
      throw new ExecutionException('Engine has not been created!')
    
    instance
  }
  
  static Environment runtime() {
    return instance.runtime
  }
  
  static Platform platform() {
    return instance.runtime.getPlatform()
  }
  
  static void msg (String fmt, Object... args) {
    getInstance().d fmt, args
    platform().nvlInfoMessage fmt, args
  }
  
  static void halt(String message='') {
    msg 'Halting program ... %s', message
    throw new Halt(message)
  }
  
  private static Engine instance
  
  private Environment runtime
  private GroovyShell shell
  private SignalSet signalSet
  private boolean runningScript
  
  private Engine(Platform platform) {
    runtime = Environment.create platform
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
      
      // Define the shell
      shell = new GroovyShell(cfg)
      shell.evaluate 'BootScript.main()'
    }
  }
  
  SignalSet getSignalSet() {
    return signalSet
  }
  
  void run(File scriptFile) {
    if (runningScript) {
      msg 'Already running a script! Ignoring order ...'
      return
    }
    runningScript = true
    ensureShellIsCreated()
    msg 'Running script \'%s\'', scriptFile
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
    runningScript = false
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
    runtime.run task
  }
  
}