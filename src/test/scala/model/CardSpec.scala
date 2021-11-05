package model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import model.Card

class CardSpec extends AnyWordSpec {
  "A Card " when {
    val card = Card(2, 5)
    "atrributes are correctly set" in {
      card.color should be(2)
      card.value should be(5)
    }
    "toString() is correctly implemented" in {
      card.toString should be("Farbe: Gelb; Wert = 5")
    }

    "equals() is works correct" when {
      val cards_to_compare = List(Card(3, 8), Card(3, 8), Card(4, 8), Card(3, 9), Card(1, 7))
      "same cards return true" in {
        cards_to_compare(0).equals(cards_to_compare(1)) should be(true)
      }
      "card with on other attribute or at third both" in {
        cards_to_compare(0).equals(cards_to_compare(2)) should be(false)
        cards_to_compare(0).equals(cards_to_compare(3)) should be(false)
        cards_to_compare(0).equals(cards_to_compare(4)) should be(false)
      }
    }
  }
}
