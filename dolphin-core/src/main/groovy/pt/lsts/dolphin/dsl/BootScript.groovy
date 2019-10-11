package pt.lsts.dolphin.dsl

import pt.lsts.dolphin.util.Debug

if (System.getenv(Debug.DEBUG_ENV_VAR) != null)
  Debug.enable(System.err,false)
  
Units.main() 
Operators.main()

_ = Engine.WILDCARD