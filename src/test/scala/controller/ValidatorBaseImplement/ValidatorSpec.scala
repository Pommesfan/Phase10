package controller.ValidatorBaseImplement

import controller.ValidatorFactoryInterface
import model.{Card, RegularCard}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import utils.Utils
import utils.Utils.{INJECT_AFTER, INJECT_TO_FRONT}

class ValidatorSpec extends AnyWordSpec {
  "A Validator" when {
    val validatorFactory:ValidatorFactoryInterface = new ValidatorFactory
    "creates a Validator" when {
      val v = validatorFactory.getValidator(1)
      "should have returned a ValidatorStrategy" when {
        val inputs = v.getNumberOfInputs
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
      val cards1 = List(RegularCard(1, 3),RegularCard(3, 3),RegularCard(4, 3),RegularCard(2, 11),RegularCard(1, 11),RegularCard(3, 11))
      val cards2 = List(RegularCard(1, 10),RegularCard(4, 10),RegularCard(1, 6),RegularCard(3, 9),RegularCard(2, 8),RegularCard(4, 9))
      "validate phase 1 with suitable cards" in {
        v.validate(cards1, indices(List(3,3))) should be(true)
      }
      "validate phase 1 with unsuitable cards" in {
        v.validate(cards2, indices(List(3,3))) should be(false)
      }
    }
    "validates phase 2" when {
      val v = validatorFactory.getValidator(2)
      val cards1 = List(RegularCard(1, 3),RegularCard(3, 3),RegularCard(4, 3),RegularCard(2, 7),RegularCard(1, 8),RegularCard(3, 9),RegularCard(1, 10))
      val cards2 = List(RegularCard(2, 5),RegularCard(1, 9),RegularCard(3, 5),RegularCard(4, 7),RegularCard(3, 8),RegularCard(1, 11),RegularCard(2, 12))
      "validate phase 2 with suitable cards" in {
        v.validate(cards1, indices(List(3,4))) should be(true)
      }
      "validate phase 2 with unsuitable cards" in {
        v.validate(cards2, indices(List(3,4))) should be(false)
      }
    }
    "validates phase 3" when {
      val v = validatorFactory.getValidator(3)
      val cards1 = List(RegularCard(1, 3),RegularCard(3, 3),RegularCard(4, 3),RegularCard(2, 3),RegularCard(1, 8),RegularCard(3, 9),RegularCard(1, 10),RegularCard(3,11))
      val cards2 = List(RegularCard(2, 5),RegularCard(1, 9),RegularCard(3, 5),RegularCard(4, 5),RegularCard(3, 8),RegularCard(1, 9),RegularCard(2, 10),RegularCard(1, 12))
      "validate phase 3 with suitable cards" in {
        v.validate(cards1, indices(List(4,4))) should be(true)
      }
      "validate phase 3 with unsuitable cards" in {
        v.validate(cards2, indices(List(4,4))) should be(false)
      }
    }
    "validates phase 4" when {
      val v = validatorFactory.getValidator(4)
      val cards1 = List(RegularCard(1, 3),RegularCard(3, 4),RegularCard(4, 5),RegularCard(2, 6),RegularCard(1, 7),RegularCard(3, 8),RegularCard(1, 9))
      val cards2 = List(RegularCard(2, 5),RegularCard(1, 6),RegularCard(3, 7),RegularCard(4, 8),RegularCard(3, 9),RegularCard(1, 10),RegularCard(2, 12))
      "validate phase 4 with suitable cards" in {
        v.validate(cards1, indices(List(7))) should be(true)
      }
      "validate phase 4 with unsuitable cards" in {
        v.validate(cards2, indices(List(7))) should be(false)
      }
    }
    "validates phase 5" when {
      val v = validatorFactory.getValidator(5)
      val cards1 = List(RegularCard(1, 2),RegularCard(3, 3),RegularCard(4, 4),RegularCard(2, 5),RegularCard(1, 6),RegularCard(3, 7),RegularCard(1, 8),RegularCard(2,9))
      val cards2 = List(RegularCard(2, 11),RegularCard(1, 12),RegularCard(3, 1),RegularCard(4, 2),RegularCard(3, 4),RegularCard(1, 5),RegularCard(2, 6),RegularCard(3,7))
      "validate phase 5 with suitable cards" in {
        v.validate(cards1, indices(List(8))) should be(true)
      }
      "validate phase 5 with unsuitable cards" in {
        v.validate(cards2, indices(List(8))) should be(false)
      }
    }
    "validates phase 6" when {
      val v = validatorFactory.getValidator(6)
      val cards1 = List(RegularCard(1, 9),RegularCard(3, 10),RegularCard(4, 11),RegularCard(2, 12),RegularCard(1, 1),RegularCard(3, 2),RegularCard(1, 3),RegularCard(1, 4),RegularCard(1, 5))
      val cards2 = List(RegularCard(2, 8),RegularCard(1, 9),RegularCard(3, 10),RegularCard(4, 11),RegularCard(3, 12),RegularCard(1, 1),RegularCard(2, 2),RegularCard(1, 3),RegularCard(1, 5))
      "validate phase 6 with suitable cards" in {
        v.validate(cards1, indices(List(9))) should be(true)
      }
      "validate phase 6 with unsuitable cards" in {
        v.validate(cards2, indices(List(9))) should be(false)
      }
    }
    "validates phase 7" when {
      val v = validatorFactory.getValidator(7)
      val cards1 = List(RegularCard(1, 4),RegularCard(3,4),RegularCard(2,4),RegularCard(2,4),RegularCard(2,9),RegularCard(3,9),RegularCard(2,9),RegularCard(4,9))
      val cards2 = List(RegularCard(2, 5),RegularCard(1, 5),RegularCard(3, 5),RegularCard(4, 5),RegularCard(3, 3),RegularCard(1, 9),RegularCard(2, 3),RegularCard(4,3))
      "validate phase 7 with suitable cards" in {
        v.validate(cards1, indices(List(4,4))) should be(true)
      }
      "validate phase 7 with unsuitable cards" in {
        v.validate(cards2, indices(List(4,4))) should be(false)
      }
    }
    "validates phase 8" when {
      val v = validatorFactory.getValidator(8)
      val cards1 = List(RegularCard(1, 3),RegularCard(1, 3),RegularCard(1, 3),RegularCard(1, 7),RegularCard(1, 8),RegularCard(1, 9),RegularCard(1, 10))
      val cards2 = List(RegularCard(4, 5),RegularCard(4, 9),RegularCard(4, 5),RegularCard(4, 7),RegularCard(2, 8),RegularCard(4, 11),RegularCard(4, 12))
      "validate phase 8 with suitable cards" in {
        v.validate(cards1, indices(List(7))) should be(true)
      }
      "validate phase 8 with unsuitable cards" in {
        v.validate(cards2, indices(List(7))) should be(false)
      }
    }
    "validates phase 9" when {
      val v = validatorFactory.getValidator(9)
      val cards1 = List(RegularCard(1, 6),RegularCard(4, 6),RegularCard(1, 6),RegularCard(2, 6),RegularCard(1, 6),RegularCard(4, 8),RegularCard(4, 8))
      val cards2 = List(RegularCard(4, 5),RegularCard(4, 5),RegularCard(4, 5),RegularCard(4, 5),RegularCard(2, 5),RegularCard(4, 11),RegularCard(4, 12))
      "validate phase 9 with suitable cards" in {
        v.validate(cards1, indices(List(5,2))) should be(true)
      }
      "validate phase 9 with unsuitable cards" in {
        v.validate(cards2, indices(List(5,2))) should be(false)
      }
    }
    "validates phase 10" when {
      val v = validatorFactory.getValidator(10)
      val cards1 = List(RegularCard(1, 3),RegularCard(3, 3),RegularCard(4, 3),RegularCard(2, 3),RegularCard(1, 3),RegularCard(3, 10),RegularCard(1, 10),RegularCard(4, 10))
      val cards2 = List(RegularCard(2, 2),RegularCard(1, 2),RegularCard(3, 2),RegularCard(4, 2),RegularCard(3, 2),RegularCard(1, 5),RegularCard(2, 11),RegularCard(1, 5))
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
        val cards = List(RegularCard(1,12),RegularCard(4,12),RegularCard(2,12))
        //stash 0 is multiple
        "can append suitabe cards" in {
          v.canAppend(cards, RegularCard(2, 12), 0, INJECT_TO_FRONT) should be(true)
          v.canAppend(cards, RegularCard(1, 12), 0, INJECT_AFTER) should be(true)
        }
        "validates unsuitable cards not toi be appended" in {
          v.canAppend(cards, RegularCard(2, 8), 0, INJECT_TO_FRONT) should be(false)
          v.canAppend(cards, RegularCard(4, 3), 0, INJECT_AFTER) should be(false)
        }
      }
      "validate append to sequence" when {
        val cards = List(RegularCard(4,9),RegularCard(2,10),RegularCard(4,11))
        //stash 1 is sequence
        "can append suitabe cards" in {
          v.canAppend(cards, RegularCard(1, 8), 1, INJECT_TO_FRONT) should be(true)
          v.canAppend(cards, RegularCard(4, 12), 1, INJECT_AFTER) should be(true)
        }
        "validates unsuitable cards not toi be appended" in {
          v.canAppend(cards, RegularCard(3, 7), 1, INJECT_TO_FRONT) should be(false)
          v.canAppend(cards, RegularCard(4, 3), 1, INJECT_AFTER) should be(false)
        }
      }
    }
  }
}
