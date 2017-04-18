package pt.lsts.nvl.dsl


import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration

import org.codehaus.groovy.control.customizers.ImportCustomizer


@TypeChecked
CompilerConfiguration configuration () {
  def cfg = new CompilerConfiguration()
  
  def ic = new ImportCustomizer()
  ic.addStaticStars 'java.lang.Math'
  ic.addStarImports 'pt.lsts.nvl.dsl'
  ic.addStaticStars 'pt.lsts.nvl.runtime.NVLVehicleType'
  
  cfg.addCompilationCustomizers(ic)
  cfg.scriptBaseClass = 'pt.lsts.nvl.dsl.TopLevel'
  cfg
}


@TypeChecked
def evalScript(File script) {
  def shell = new GroovyShell(configuration())
  shell.evaluate(script)
}

evalScript new File('examples/select.nvl')
