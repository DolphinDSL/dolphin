package pt.lsts.dolphin.dsl


import spock.lang.Specification

class UnitsTest extends Specification {

  def setupSpec() {
    Units.main()
  }

  def 'Distance Units' () {
    expect:
    1.meters == 1
    5.meters == 5
    1.kilometers == 1000
    5.kilometers == 5000
    3000.meters - 3.kilometers == 0
  }

  def 'Angle Units' () {
    expect:
    1.degrees == 1
    5.degrees == 5
    Math.PI.radians == 180.degrees
    (0.5*Math.PI).radians == 90.degrees
    180.degrees - Math.PI.radians == 0
  }
  
  def 'Orientation Units' () {
    expect:
    1.north == 1
    1.south == -1
    1.east == 1
    1.west == -1
    5.north + 5.south == 0
    5.east + 5.west == 0
  }
  
  def 'Time Units' () {
    expect:
    1.seconds == 1
    1.minutes == 60
    1.hours == 3600
    1.days == 24 * 3600
    5.seconds + 5.minutes == 305
    5.hours + 15.minutes == 5.25.hours
    2.days - 48.hours == 0
    2.5.minutes - 150.seconds == 0
  }
  
  def 'Percentage' () {
    expect:
    1.percent == 0.01
    100.percent == 1.0
    33.percent == 0.33
    37.5.percent == 0.375
  }
}
