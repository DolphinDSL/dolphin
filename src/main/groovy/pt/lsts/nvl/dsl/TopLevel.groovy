package pt.lsts.nvl.dsl

import groovy.transform.TypeChecked

@TypeChecked
def select(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=Selection) Closure cl) {
  new Selection().buildAndExecute(cl)
}
