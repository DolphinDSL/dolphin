package pt.lsts.nvl.dsl



import spock.lang.Specification

class SignalSetTest extends Specification {

  SignalSet theSUT

  def setup() {
    theSUT = new SignalSet()
  }

  def 'Simple Post' () {
    when:
       theSUT.post('x', 1)
    then:
       theSUT.test('x', 1)
       !theSUT.test ('x', 0)
       theSUT.poll('x')
       !theSUT.poll('y')
  }
  
  def 'Multiple Post' () {
    when:
       theSUT.post(x:1, y:2)
    then:
       theSUT.poll('x')
       theSUT.poll('y')
       !theSUT.poll('z')
       theSUT.test('x', 1)
       theSUT.test('y', 2)
       theSUT.test(x:1, y:2)
       !theSUT.test(x:1, y:0) 
       !theSUT.test(x:1, y:2, z:3) 
       
  }
  
  def 'Simple Post Followed By Consume' () {
    when:
       theSUT.post('x', 1)
    then:
       theSUT.poll('x')
       theSUT.poll(x : { it > 0 })
       theSUT.poll(x : { v -> v > 0 })
       !theSUT.poll(x : { it != 1 })
       !theSUT.poll(x : { v -> v != 1 })
       !theSUT.poll(x : { it <= 0 })
       !theSUT.consume('x', 0) 
       theSUT.consume('x', 1)
       !theSUT.test('x', 1)
       !theSUT.poll('x')
       
  }
  
  def 'Multiple Post Followed By Consume' () {
    when:
       theSUT.post (x:1, y:2, z:3)
       theSUT.consume(x:1, z:3)
    then:
       !theSUT.test('x', 1)
       theSUT.test 'y', 2
       !theSUT.test('z', 3)
       !theSUT.test(x:1, z:3)
       theSUT.consume y:2 
       !theSUT.consume(y:2)
  }

  
}
