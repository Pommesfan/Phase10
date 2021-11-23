package controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import model.Card

class ValidatorSpec extends AnyWordSpec {
  "A Validator" when {
    val v = Validator.getValidator(1)
    val cards1 = List(Card(1,3),Card(3,3),Card(4,3),Card(2,11),Card(1,11),Card(3,11))
    val cards2 = List(Card(1,10),Card(4,10),Card(1,6),Card(3,9),Card(2,8),Card(4,9))
    val indices = List(0, 1, 2, 3, 4, 5)
    "validate phase 1 with suitable cards" in {
      v.validate(cards1, indices) should be(true)
    }
    "validate phase 1 with unsuitable cards" in {
      v.validate(cards2, indices) should be(false)
    }
  }
}
