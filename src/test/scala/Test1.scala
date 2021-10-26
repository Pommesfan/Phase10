import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class Test1:
    msg should be("I was compiled by Scala 3. :)")
    nextPlayer(0, 4) should be(1)
    nextPlayer(1, 4) should be(2)
    nextPlayer(2, 4) should be(3)
    nextPlayer(3, 4) should be(0)
