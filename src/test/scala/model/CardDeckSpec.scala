package model
import controller.ControllerBaseImplement.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import utils.Utils.INJECT_TO_FRONT

class CardDeckSpec extends AnyWordSpec {
  "A PlayerCardDeck" when {
    val c = new Controller
    val cardList = List.fill(2)(List.fill(10)(c.createCard))
    val playerCardDeck = new PlayerCardDeck(cardList)
    "cardList correctly set" in {
      playerCardDeck.cards.size should be(2)
      playerCardDeck.cards(0).size should be(10)
      playerCardDeck.cards(1).size should be(10)
    }
    "switch card" when {
      val newCard = new Card(1,8)
      val resNewDeck = playerCardDeck.switchCard(0,0, newCard)
      def newCardDeck = resNewDeck._1
      def removedCard = resNewDeck._2
      "should still be 10 cards" in {
        newCardDeck.cards(0).size should be(10)
      }
      "should have switched correctly" in {
        removedCard should be(cardList(0)(0))
        newCardDeck.cards(0)(0) should be(newCard)
      }
    }
    "remove selected cards" when {
      val resNewDeck = playerCardDeck.removeCards(List(List(0,1)), 0)
      def newCardDeck = resNewDeck._1
      def removedCards = resNewDeck._2
      "should still contain two players" in {
        newCardDeck.cards.size should be(2)
      }
      "player one has 8 cards left and player two has still 10" in {
        newCardDeck.cards(0).size should be(8)
        newCardDeck.cards(1).size should be(10)
      }
      "removed cards should be the selected one" in {
        removedCards(0)(0) should be(cardList(0)(0))
        removedCards(0)(1) should be(cardList(0)(1))
      }
      "remove a single card" when {
        val resNewCardDeck2 = newCardDeck.removeSingleCard(0,0)
        def newCardDeck2 = resNewCardDeck2._1
        def removedCard = resNewCardDeck2._2
        "should still contain two players" in {
          newCardDeck2.cards.size should be(2)
        }
        "player one has 7 cards left and player two has still 10" in {
          newCardDeck2.cards(0).size should be(7)
          newCardDeck2.cards(1).size should be(10)
        }
        "return card is the one to be removed" in {
          removedCard should be(newCardDeck.cards(0)(0))
        }
      }
    }
  }
  "A DiscardedCardDeck" when {
    val discardedCardDeck = new DiscardedCardDeck(List.fill(2)(None))
    "initially have two empty substashes" in {
      discardedCardDeck.cards.size should be(2)
      discardedCardDeck.cards(0) should be(None)
      discardedCardDeck.cards(1) should be(None)
    }
    "add a stash" when {
      val stash = List(
        List(new Card(1,6), new Card(1,7), new Card(2, 5), new Card(3, 1)),
        List(new Card(4,3), new Card(1,5), new Card(2,11), new Card(3,6))
      )
      val newDiscardedDeck = discardedCardDeck.setCards(0, stash)
      "cards correctly set to player one" in {
        newDiscardedDeck.cards(0).get should be(stash)
        newDiscardedDeck.cards(1) should be(None)
      }
      "add a single card to the setted stash" when {
        val newCard = new Card(3, 12)
        val newDiscardedDeck2 = newDiscardedDeck.appendCard(newCard, 0, 1, INJECT_TO_FRONT)
        "second player should still have None" in {
          newDiscardedDeck.cards(1) should be(None)
        }
        "first player should have now a stash sized 4 and 5 and first card of second stash is the new one" in {
          val stash = newDiscardedDeck2.cards(0).get
          stash.size should be(2)
          stash(0).size should be(4)
          stash(1).size should be(5)
          stash(1)(0) should be(newCard)
        }
      }
    }
  }
}
