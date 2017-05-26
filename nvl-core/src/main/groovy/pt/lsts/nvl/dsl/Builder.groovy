package pt.lsts.nvl.dsl

import groovy.lang.Closure
import pt.lsts.nvl.util.Debuggable

@DSLClass
abstract class Builder<T> implements Debuggable {
  abstract T build()

  final T build(Closure cl) {
    def code = cl.rehydrate(this, this, this)
    code.resolveStrategy = Closure.DELEGATE_ONLY
    code()
    build()
  }

}


