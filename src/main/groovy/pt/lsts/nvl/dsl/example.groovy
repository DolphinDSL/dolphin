package pt.lsts.nvl.dsl;



select {
  time 20
  vehicle {
    type AUV
    payload 'SideScan', 'GPS'
  }
  vehicle {
    type Type.UAV
    payload 'Camera', 'GPS'
  }
}

