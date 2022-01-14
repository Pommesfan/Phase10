package controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import model.Card
import utils.Utils
import Utils.{INJECT_AFTER, INJECT_TO_FRONT}
import controller.ValidatorBaseImplement.ValidatorFactory

class ValidatorSpec extends AnyWordSpec {
  "A Validator" when {
    val validatorFactory:ValidatorFactoryInterface = new ValidatorFactory
    "creates a Validator" when {
      val v = validatorFactory.getValidator(1)
      "should have returned a ValidatorStrategy" when {
        val inputs = v.getNumberOfInputs()
        "Has set up a validator for phase 1" in {
          inputs.size should be(2)
          inputs(0) should be(3)
          inputs(1) should be(3)
        }
      }
    }
    def indices(numberOfCards: List[Int]): List[List[Int]] =
      var l = List[List[Int]]()
      var i = 0
      for (n <- numberOfCards)
        l = l :+ List.range(i, i+n)
        i += n
      l

    "validates phase 1" when {
      val v = validatorFactory.getValidator(1)
      val cards1 = List(Card(1, 3), Card(3, 3), Card(4, 3), Card(2, 11), Card(1, 11), Card(3, 11))
      val cards2 = List(Card(1, 10), Card(4, 10), Card(1, 6), Card(3, 9), Card(2, 8), Card(4, 9))
      "validate phase 1 with suitable cards" in {
        v.validate(cards1, indices(List(3,3))) should be(true)
      }
      "validate phase 1 with unsuitable cards" in {
        v.validate(cards2, indices(List(3,3))) should be(false)
      }
    }
    "validates phase 2" when {
      val v = validatorFactory.getValidator(2)
      val cards1 = List(Card(1, 3), Card(3, 3), Card(4, 3), Card(2, 7), Card(1, 8), Card(3, 9), Card(1, 10))
      val cards2 = List(Card(2, 5), Card(1, 9), Card(3, 5), Card(4, 7), Card(3, 8), Card(1, 11), Card(2, 12))
      "validate phase 2 with suitable cards" in {
        v.validate(cards1, indices(List(3,4))) should be(true)
      }
      "validate phase 2 with unsuitable cards" in {
        v.validate(cards2, indices(List(3,4))) should be(false)
      }
    }
    "validates phase 3" when {
      val v = validatorFactory.getValidator(3)
      val cards1 = List(Card(1, 3), Card(3, 3), Card(4, 3), Card(2, 3), Card(1, 8), Card(3, 9), Card(1, 10), Card(3,11))
      val cards2 = List(Card(2, 5), Card(1, 9), Card(3, 5), Card(4, 5), Card(3, 8), Card(1, 9), Card(2, 10), Card(1, 12))
      "validate phase 3 with suitable cards" in {
        v.validate(cards1, indices(List(4,4))) should be(true)
      }
      "validate phase 3 with unsuitable cards" in {
        v.validate(cards2, indices(List(4,4))) should be(false)
      }
    }
    "validates phase 4" when {
      val v = validatorFactory.getValidator(4)
      val cards1 = List(Card(1, 3), Card(3, 4), Card(4, 5), Card(2, 6), Card(1, 7), Card(3, 8), Card(1, 9))
      val cards2 = List(Card(2, 5), Card(1, 6), Card(3, 7), Card(4, 8), Card(3, 9), Card(1, 10), Card(2, 12))
      "validate phase 4 with suitable cards" in {
        v.validate(cards1, indices(List(7))) should be(true)
      }
      "validate phase 4 with unsuitable cards" in {
        v.validate(cards2, indices(List(7))) should be(false)
      }
    }
    "validates phase 5" when {
      val v = validatorFactory.getValidator(5)
      val cards1 = List(Card(1, 2), Card(3, 3), Card(4, 4), Card(2, 5), Card(1, 6), Card(3, 7), Card(1, 8), Card(2,9))
      val cards2 = List(Card(2, 11), Card(1, 12), Card(3, 1), Card(4, 2), Card(3, 4), Card(1, 5), Card(2, 6), Card(3,7))
      "validate phase 5 with suitable cards" in {
        v.validate(cards1, indices(List(8))) should be(true)
      }
      "validate phase 5 with unsuitable cards" in {
        v.validate(cards2, indices(List(8))) should be(false)
      }
    }
    "validates phase 6" when {
      val v = validatorFactory.getValidator(6)
      val cards1 = List(Card(1, 9), Card(3, 10), Card(4, 11), Card(2, 12), Card(1, 1), Card(3, 2), Card(1, 3), Card(1, 4), Card(1, 5))
      val cards2 = List(Card(2, 8), Card(1, 9), Card(3, 10), Card(4, 11), Card(3, 12), Card(1, 1), Card(2, 2), Card(1, 3), Card(1, 5))
      "validate phase 6 with suitable cards" in {
        v.validate(cards1, indices(List(9))) should be(true)
      }
      "validate phase 6 with unsuitable cards" in {
        v.validate(cards2, indices(List(9))) should be(false)
      }
    }
    "validates phase 7" when {
      val v = validatorFactory.getValidator(7)
      val cards1 = List(Card(1, 4),Card(3,4),Card(2,4),Card(2,4), Card(2,9), Card(3,9),Card(2,9),Card(4,9))
      val cards2 = List(Card(2, 5), Card(1, 5), Card(3, 5), Card(4, 5), Card(3, 3), Card(1, 9), Card(2, 3), Card(4,3))
      "validate phase 7 with suitable cards" in {
        v.validate(cards1, indices(List(4,4))) should be(true)
      }
      "validate phase 7 with unsuitable cards" in {
        v.validate(cards2, indices(List(4,4))) should be(false)
      }
    }
    "validates phase 8" when {
      val v = validatorFactory.getValidator(8)
      val cards1 = List(Card(1, 3), Card(1, 3), Card(1, 3), Card(1, 7), Card(1, 8), Card(1, 9), Card(1, 10))
      val cards2 = List(Card(4, 5), Card(4, 9), Card(4, 5), Card(4, 7), Card(2, 8), Card(4, 11), Card(4, 12))
      "validate phase 8 with suitable cards" in {
        v.validate(cards1, indices(List(7))) should be(true)
      }
      "validate phase 8 with unsuitable cards" in {
        v.validate(cards2, indices(List(7))) should be(false)
      }
    }
    "validates phase 9" when {
      val v = validatorFactory.getValidator(9)
      val cards1 = List(Card(1, 6), Card(4, 6), Card(1, 6), Card(2, 6), Card(1, 6), Card(4, 8), Card(4, 8))
      val cards2 = List(Card(4, 5), Card(4, 5), Card(4, 5), Card(4, 5), Card(2, 5), Card(4, 11), Card(4, 12))
      "validate phase 9 with suitable cards" in {
        v.validate(cards1, indices(List(5,2))) should be(true)
      }
      "validate phase 9 with unsuitable cards" in {
        v.validate(cards2, indices(List(5,2))) should be(false)
      }
    }
    "validates phase 10" when {
      val v = validatorFactory.getValidator(10)
      val cards1 = List(Card(1, 3), Card(3, 3), Card(4, 3), Card(2, 3), Card(1, 3), Card(3, 10), Card(1, 10), Card(4, 10))
      val cards2 = List(Card(2, 2), Card(1, 2), Card(3, 2), Card(4, 2), Card(3, 2), Card(1, 5), Card(2, 11), Card(1, 5))
      "validate phase 10 with suitable cards" in {
        v.validate(cards1, indices(List(5,3))) should be(true)
      }
      "validate phase 10 with unsuitable cards" in {
        v.validate(cards2, indices(List(5,3))) should be(false)
      }
    }
    "validate to inject card to another player in" when {
      val v = validatorFactory.getValidator(2)
      "validate append to multiples" when {
        val cards = List(Card(1,12),Card(4,12),Card(2,12))
        //stash 0 is multiple
        "can append suitabe cards" in {
          v.canAppend(cards, Card(2, 12), 0, INJECT_TO_FRONT) should be(true)
          v.canAppend(cards, Card(1, 12), 0, INJECT_AFTER) should be(true)
        }
        "validates unsuitable cards not toi be appended" in {
          v.canAppend(cards, Card(2, 8), 0, INJECT_TO_FRONT) should be(false)
          v.canAppend(cards, Card(4, 3), 0, INJECT_AFTER) should be(false)
        }
      }
      "validate append to sequence" when {
        val cards = List(Card(4,9),Card(2,10),Card(4,11))
        //stash 1 is sequence
        "can append suitabe cards" in {
          v.canAppend(cards, Card(1, 8), 1, INJECT_TO_FRONT) should be(true)
          v.canAppend(cards, Card(4, 12), 1, INJECT_AFTER) should be(true)
        }
        "validates unsuitable cards not toi be appended" in {
          v.canAppend(cards, Card(3, 7), 1, INJECT_TO_FRONT) should be(false)
          v.canAppend(cards, Card(4, 3), 1, INJECT_AFTER) should be(false)
        }
      }
    }
  }
}
