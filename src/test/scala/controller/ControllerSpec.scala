package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import model.Card

class ControllerSpec extends AnyWordSpec {
  "A Controller" when {
    val controller = new Controller

    "nextplayer return following number but zero if it would be beyond number of players" in {
      controller.nextPlayer(0, 4) should be(1)
      controller.nextPlayer(1, 4) should be(2)
      controller.nextPlayer(2, 4) should be(3)
      controller.nextPlayer(3, 4) should be(0)
    }

    "create an card randomly" when {
      val c = controller.createCard
      "colors ranges from 1 to 4 and values from 1 to 12" in {
        c.color >= 1 && c.color <= 4 should be(true)
        c.value >= 1 && c.value <= 12 should be(true)
      }
    }

    "create card stahses for players" when {
      val stash = controller.createCardStash(2)
      "number of cardstashes for player is number of plyers" in {
        stash.size should be(2)
      }
      "size of every stash is 10" in {
        for (l <- stash)
          l.size should be(10)
      }
    }

    "changing cards" when {
      //Test of changing cards
      def CHANGED_CARD = 8
      def PLAYER_INDEX = 1
      def NUMBER_OF_PLAYERS = 2

      val openCard = controller.createCard
      val stash2 = controller.createCardStash(NUMBER_OF_PLAYERS)
      val result1 = controller.change_card(CHANGED_CARD, PLAYER_INDEX, openCard, stash2, "open")
      val result2 = controller.change_card(CHANGED_CARD, PLAYER_INDEX, openCard, stash2, "new")

      //other cards but selected should be the same

      def check_other_cards(newStash:List[List[Card]]) =
        (0 until NUMBER_OF_PLAYERS).foreach(player =>
          (0 until stash2.size).foreach(card =>
            if (!(player == PLAYER_INDEX && card == CHANGED_CARD))
              stash2(player)(card).equals(newStash(player)(card)) should be(true)
          )
        )

      "replaces card with open card" when {
        def newStash = result1._1
        def newOpenCard = result1._2

        "selected card is replaced with open card" in {
          newStash(PLAYER_INDEX)(CHANGED_CARD).equals(openCard) should be(true)
        }

        "new open card is one which was dropped by replacement in last statement" in {
          newOpenCard.equals(stash2(PLAYER_INDEX)(CHANGED_CARD)) should be(true)
        }

        "other cards ecxept of selected from current player are not changed" in {
          check_other_cards(newStash)
        }
      }
      "replaces card with new card" when {
        def newStash = result2._1
        def newOpenCard = result2._2

        "new open card is one which was dropped by replacement in last statement" in {
          newOpenCard.equals(stash2(PLAYER_INDEX)(CHANGED_CARD)) should be(true)
        }

        "other cards ecxept of selected from current player are not changed" in {
          check_other_cards(newStash)
        }
      }
    }
    "moves cards from stash to discarded-stash" when {
      val NUMBER_OF_PLAYERS = 2
      val stash = controller.createCardStash(NUMBER_OF_PLAYERS)
      val discardedStash = List.fill(NUMBER_OF_PLAYERS)(List[Card]())
      val INDICES = List(0,1,2)
      val res = controller.discard_cards(1, INDICES, stash, discardedStash)
      "moves cards of current player correctly" in {
        val newStash = res._1
        val newDiscardedStash = res._2
        newStash(1).size should be(7)
        newStash(0).size should be(10)
        newDiscardedStash(1).size should be(3)
        newDiscardedStash(0).size should be(0)
      }
    }
  }
}
