package pt.lsts.nvl.dsl

import pt.lsts.nvl.util.Debuggable

@DSLClass
abstract class Instruction<T> implements Debuggable {
  abstract T execute()

  final void build(Closure cl) {
    def code = cl.rehydrate(this, this, this)
    code.resolveStrategy = Closure.DELEGATE_ONLY
    code()
  }

  final T buildAndExecute(Closure cl) {
    build cl
    execute()
  }

}


