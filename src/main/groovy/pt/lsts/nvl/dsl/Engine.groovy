package pt.lsts.nvl.dsl


import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration

import org.codehaus.groovy.control.customizers.ImportCustomizer


/**
 * The NVL shell.
 */
@CompileStatic
@TypeChecked
@Singleton(strict=false)
class Engine {
  
  GroovyShell shell
  
  private Engine() {
    // Imports
    def ic = new ImportCustomizer()
    ic.addStaticStars 'java.lang.Math'
    ic.addStarImports 'pt.lsts.nvl.dsl'
    ic.addStaticStars 'pt.lsts.nvl.runtime.NVLVehicleType'
  
    // Compiler configuration
    def cfg = new CompilerConfiguration()
    cfg.addCompilationCustomizers(ic)
    cfg.scriptBaseClass = 'pt.lsts.nvl.dsl.BaseScript'
    
    // Define the shell
    shell = new GroovyShell(cfg)
  }

  void run(File script) {
    shell.evaluate(script)
  }
  
  public static void main(String... args) {
    for (String f : args) {
      Engine.instance.run new File(f)
    }
  }
}