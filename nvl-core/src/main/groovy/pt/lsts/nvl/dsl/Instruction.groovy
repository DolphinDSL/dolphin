package pt.lsts.nvl.dsl

@DSLClass
abstract class Instruction<T> {
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


