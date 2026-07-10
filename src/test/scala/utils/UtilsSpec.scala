package utils

import model.{Card, JokerCard, RegularCard}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import Utils.{INJECT_AFTER, INJECT_TO_FRONT}
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
    val indices = "2 6 8 : 4 7 9 5"
    val inputs = List(3,4)
    val result = List(List(2,6,8), List(4,7,9,5))
    "flat int list turn to 2d-list with given lengths" in {
      Utils.makeGroupedIndexList(indices, inputs) should be(result)
    }
  }
  "checks if cards are a sequence" when {
    "check with suitable cards" in {
      val cards1 = List(RegularCard(3,5),RegularCard(1,6),RegularCard(2,7))
      val cards2 = List(RegularCard(4,12),JokerCard(),RegularCard(1,2))
      Utils.resolveSequence(cards1) should be(true)
      Utils.resolveSequence(cards2) should be(true)
    }
    "check with unsuitable cards" in {
      val cards1 = List(RegularCard(1,8),RegularCard(4,9),RegularCard(2,11))
      val cards2 = List(RegularCard(4,3),JokerCard(),RegularCard(1,6))
      Utils.resolveSequence(cards1) should be(false)
      Utils.resolveSequence(cards2) should be(false)
    }
  }
  "checks if cards are multiples" when {
    "check with suitable cards" in {
      val cards1 = List(RegularCard(1,9),RegularCard(3,9),RegularCard(4,9))
      val cards2 = List(RegularCard(1,9),JokerCard(),RegularCard(4,9))
      Utils.resolveSameValue(cards1, c => c.value) should be(true)
      Utils.resolveSameValue(cards2, c => c.value) should be(true)
    }
    "check with unsuitable cards" in {
      val cards1 = List(RegularCard(2,8),RegularCard(1,7),RegularCard(3,8))
      val cards2 = List(RegularCard(2,3),JokerCard(),RegularCard(3,4))
      Utils.resolveSameValue(cards1, c => c.value) should be(false)
      Utils.resolveSameValue(cards2, c => c.value) should be(false)
    }
  }
  "checks if cards have same color" when {
    "check with suitable cards" in {
      val cards1 = List(RegularCard(3,9),RegularCard(3,1),RegularCard(3,12))
      val cards2 = List(RegularCard(1,3),JokerCard(),RegularCard(1,2))
      Utils.resolveSameValue(cards1, c => c.color) should be(true)
      Utils.resolveSameValue(cards2, c => c.color) should be(true)
    }
    "check with unsuitable cards" in {
      val cards1 = List(RegularCard(1,5),RegularCard(1,2),RegularCard(4,11))
      val cards2 = List(RegularCard(3,5),JokerCard(),RegularCard(1,11))
      Utils.resolveSameValue(cards1, c => c.color) should be(false)
      Utils.resolveSameValue(cards2, c => c.color) should be(false)
    }
  }
  "fitToSequence checks if card can be injected in front or after an sequence" when {
    val l1 = List(RegularCard(1,6), RegularCard(4,7), RegularCard(3,8))
    val l2 = List(RegularCard(1,10), RegularCard(4,11), RegularCard(3,12))
    val l3 = List(RegularCard(1,1), RegularCard(4,2), RegularCard(3,3))
    "can append card in front of or after" in {
      Utils.fitToSequence(l1, RegularCard(3,5), INJECT_TO_FRONT, 0, l1.head) should be(true)
      Utils.fitToSequence(l1, RegularCard(3,9), INJECT_AFTER, 0, l1.head) should be(true)
      Utils.fitToSequence(l1, RegularCard(3, 5), INJECT_TO_FRONT, 0, l1.head) should be(true)
      Utils.fitToSequence(l1, RegularCard(3,3), INJECT_TO_FRONT, 0, l1.head) should be(false)
      Utils.fitToSequence(l1, RegularCard(3,11), INJECT_AFTER, 0, l1.head) should be(false)
    }
    "edge case: can append 1 after 12 and 12 in front of 1" in {
      Utils.fitToSequence(l2, RegularCard(3,1), INJECT_AFTER, 0, l2.head) should be(true)
      Utils.fitToSequence(l3, RegularCard(2,12), INJECT_TO_FRONT, 0, l3.head) should be(true)
    }
  }
}
