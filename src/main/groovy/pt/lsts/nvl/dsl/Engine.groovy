package pt.lsts.nvl.dsl


import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import pt.lsts.nvl.runtime.NVLRuntime


/**
 * The NVL engine.
 */
@CompileStatic
@TypeChecked
class Engine {
  
  private GroovyShell shell
  
  NVLRuntime runtime;
  
  static final Engine instance = new Engine()
  
  private Engine() {
    
  }
 
  void run(File script) {
    if (shell == null) {
      // Imports
      def ic = new ImportCustomizer()
      ic.with {
        addStaticStars 'java.lang.Math'
        addStarImports 'pt.lsts.nvl.dsl'
        addStaticStars 'pt.lsts.nvl.dsl.BaseScript'
        addStaticStars 'pt.lsts.nvl.runtime.NVLVehicleType'
      }
      // Compiler configuration
      def cfg = new CompilerConfiguration()
      cfg.with {
        addCompilationCustomizers ic
        scriptBaseClass = 'pt.lsts.nvl.dsl.BaseScript'
      }
      // Define the shell
      shell = new GroovyShell(cfg)      
      shell.evaluate 'BaseScript.main()'
    }
    shell.evaluate script
  }
  
  public static void main(String... args) {
    
    for (String f : args) {
      Engine.instance.run new File(f)
    }
  }
}