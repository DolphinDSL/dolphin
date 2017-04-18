package pt.lsts.nvl.dsl

@DSLClass
abstract class Instruction {
  abstract void execute()

  final void build(Closure cl) {
    def code = cl.rehydrate(this, this, this)
    code.resolveStrategy = Closure.DELEGATE_ONLY
    code()
  }

  final void buildAndExecute(Closure cl) {
    build cl
    execute()
  }

}


