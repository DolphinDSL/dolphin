package pt.lsts.nvl;



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

