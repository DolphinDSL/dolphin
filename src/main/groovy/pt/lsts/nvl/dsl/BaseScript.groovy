package pt.lsts.nvl.dsl

import groovy.transform.TypeChecked

NumberUnits.main() 

// DSL instructions
@TypeChecked
def select(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Selection) Closure cl) {
  new Selection().buildAndExecute(cl)
}
