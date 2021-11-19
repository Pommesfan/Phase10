package model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import model.Card

class CardSpec extends AnyWordSpec {
  "A Card " when {
    val cards_to_compare = List(Card(3, 8), Card(3, 8), Card(4, 8), Card(3, 9), Card(1, 7), Card(2, 5), Card(1, 10))
    "atrributes are correctly set" in {
      cards_to_compare(5).color should be(2)
      cards_to_compare(5).value should be(5)
    }
    "toString() is correctly implemented" in {
      cards_to_compare(5).toString should be("Farbe: Gelb; Wert = 5")
      cards_to_compare(1).toString should be("Farbe: Blau; Wert = 8")
      cards_to_compare(2).toString should be("Farbe: Grün; Wert = 8")
      cards_to_compare(6).toString should be("Farbe: Rot; Wert = 10")
    }

    "equals() is works correct" when {
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
