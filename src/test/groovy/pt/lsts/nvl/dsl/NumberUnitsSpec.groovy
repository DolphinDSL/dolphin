package pt.lsts.nvl.dsl

import spock.lang.Specification

class NumberUnitsSpec extends Specification {

  def setupSpec() {
    NumberUnits.main()
  }

  def "Distance Units" () {
    expect:
    1.meters == 1
    5.meters == 5
    1.kilometers == 1000
    5.kilometers == 5000
    3000.meters - 3.kilometers == 0
  }

  def "Angle Units" () {
    expect:
    1.degrees == 1
    5.degrees == 5
    Math.PI.radians == 180.degrees
    (0.5*Math.PI).radians == 90.degrees
    180.degrees - Math.PI.radians == 0
  }
  
  def "Orientation Units" () {
    expect:
    1.north == 1
    1.south == -1
    1.east == 1
    1.west == -1
    5.north + 5.south == 0
    5.east + 5.west == 0
  }
}
