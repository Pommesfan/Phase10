package controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import model.Card

class ValidatorSpec extends AnyWordSpec {
  "A Validator" when {
    def indices(numberOfCards: Int) = (0 until numberOfCards).toList

    "validates phase 1" when {
      val v = Validator.getValidator(1)
      val cards1 = List(Card(1, 3), Card(3, 3), Card(4, 3), Card(2, 11), Card(1, 11), Card(3, 11))
      val cards2 = List(Card(1, 10), Card(4, 10), Card(1, 6), Card(3, 9), Card(2, 8), Card(4, 9))
      "validate phase 1 with suitable cards" in {
        v.validate(cards1, indices(6)) should be(true)
      }
      "validate phase 1 with unsuitable cards" in {
        v.validate(cards2, indices(6)) should be(false)
      }
    }
    "validates phase 2" when {
      val v = Validator.getValidator(2)
      val cards1 = List(Card(1, 3), Card(3, 3), Card(4, 3), Card(2, 7), Card(1, 8), Card(3, 9), Card(1, 10))
      val cards2 = List(Card(2, 5), Card(1, 9), Card(3, 5), Card(4, 7), Card(3, 8), Card(1, 11), Card(2, 12))
      "validate phase 2 with suitable cards" in {
        v.validate(cards1, indices(7)) should be(true)
      }
      "validate phase 2 with unsuitable cards" in {
        v.validate(cards2, indices(7)) should be(false)
      }
    }
  }
}
