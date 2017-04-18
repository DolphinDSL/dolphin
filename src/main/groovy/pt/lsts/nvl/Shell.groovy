package pt.lsts.nvl


import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration

import org.codehaus.groovy.control.customizers.ImportCustomizer


CompilerConfiguration configuration () {
  def cfg = new CompilerConfiguration()
  
  def ic = new ImportCustomizer()
  ic.addStaticStars 'java.lang.Math'
  ic.addStarImports 'dslSketch'
  ic.addStaticStars 'dslSketch.Type'
  
  cfg.addCompilationCustomizers(ic)
  cfg.scriptBaseClass = 'dslSketch.TopLevel'
  cfg
}


@TypeChecked
def evalScript(File script) {
  def shell = new GroovyShell(configuration())
  shell.evaluate(script)
}

evalScript new File('src/dslSketch/example.groovy')
