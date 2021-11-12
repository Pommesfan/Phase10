import controller.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import model.Card

class Test1 extends AnyWordSpec:
    "A Phase 10 Cardgame" when {
        Main.msg should be("I was compiled by Scala 3. :)")
    }