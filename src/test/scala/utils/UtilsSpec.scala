package utils

import model.Card
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
class UtilsSpec extends AnyWordSpec {
  "make inverse index list" when {
    val indices = List(4,5,6,7)
    "returns indexes from 1 to 10 except of the ones just defined" in {
      Utils.inverseIndexList(indices, 10) should be(List(0,1,2,3,8,9))
    }
  }
  "checks if list has no value in twice" when {
    val l1 = List(4,3,6,9,7,1)
    val l2 = List(3,3,8,6,5,9,7,6)

    "check unique list" in {
      Utils.indexesUnique(l1) should be(true)
    }
    "check list one value twice" in {
      Utils.indexesUnique(l2) should be(false)
    }
  }
  "groups cards indices to cardGroups from flat Int-list" when {
    val indices = "2 6 8 ; 4 7 9 5"
    val inputs = List(3,4)
    val result = List(List(2,6,8), List(4,7,9,5))
    "flat int list turn to 2d-list with given lengths" in {
      Utils.makeGroupedIndexList(indices, inputs) should be(result)
    }
  }
  "checks if cards are a sequence" when {
    "check with suitable cards" in {
      val cards = List(Card(3,5),Card(1,6),Card(2,7))
      Utils.resolveSequence(cards) should be(true)
    }
    "check with unsuitable cards" in {
      val cards = List(Card(1,8),Card(4,9),Card(2,11))
      Utils.resolveSequence(cards) should be(false)
    }
  }
  "checks if cards are multiples" when {
    "check with suitable cards" in {
      val cards = List(Card(1,9),Card(3,9),Card(4,9))
      Utils.resolveMultiples(cards) should be(true)
    }
    "check with unsuitable cards" in {
      val cards = List(Card(2,8),Card(1,7),Card(3,8))
      Utils.resolveMultiples(cards) should be(false)
    }
  }
  "checks if cards have same color" when {
    "check with suitable cards" in {
      val cards = List(Card(3,9),Card(3,1),Card(3,12))
      Utils.resolveSameColor(cards) should be(true)
    }
    "check with unsuitable cards" in {
      val cards = List(Card(1,5),Card(1,2),Card(4,11))
      Utils.resolveSameColor(cards) should be(false)
    }
  }
}
