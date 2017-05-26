package pt.lsts.nvl.dsl


import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import pt.lsts.nvl.runtime.NVLPlatform
import pt.lsts.nvl.runtime.NVLRuntime
import pt.lsts.nvl.runtime.tasks.Task
import pt.lsts.nvl.util.Debuggable
import pt.lsts.nvl.runtime.NVLExecutionException

/**
 * The NVL engine.
 */
@DSLClass
class NVLEngine implements Debuggable {
  
  static NVLEngine create(NVLPlatform platform) {
    if (instance != null)
      throw new NVLExecutionException('Engine already created!')
      
    instance = new NVLEngine(platform)
    msg 'Engine on !'
    instance
  }
  
  static NVLEngine getInstance() {
    if (instance == null)
      throw new NVLExecutionException('Engine has not been created!')
    
    instance
  }
  
  static NVLRuntime runtime() {
    return instance.runtime
  }
  
  static NVLPlatform platform() {
    return instance.runtime.getPlatform()
  }
  
  static void msg (String fmt, Object... args) {
    getInstance().d fmt, args
    platform().nvlInfoMessage fmt, args
  }
  
  static void halt(String message='') {
    msg 'Halting program ... \'%s\'', message
    throw new HaltProgramException(message)
  }
  
  private static NVLEngine instance
  
  private NVLEngine(NVLPlatform platform) {
    runtime = NVLRuntime.create platform
  }

  private NVLRuntime runtime;
  private GroovyShell shell
 
  private void ensureShellIsCreated() {
    if (shell == null) {
      // Imports
      def ic = new ImportCustomizer()
      ic.with {
        addStaticStars 'java.lang.Math'
        addStaticStars 'pt.lsts.nvl.dsl.DSLInstructions'
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
  
  void run(File scriptFile) {
    ensureShellIsCreated()
    msg 'Running script \'%s\'', scriptFile
    try {
      shell.evaluate scriptFile
    }
    catch (HaltProgramException e) {
      println 'Program halted: ' + e.getMessage() 
    }
    catch (Throwable e) {
      println 'Program terminated with unexpected exception!' 
      e.printStackTrace(System.out)
    }
  }
  
  void bind(String var, Object value) {
    ensureShellIsCreated()
    shell.setVariable var, value
  }
  
  void unbind(String var) {
    ensureShellIsCreated()
    shell.context.variables.remove var
  }
  
  void run(Task task) {
    runtime.run task
  }
  
}