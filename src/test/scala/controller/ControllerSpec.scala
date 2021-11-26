package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import model.{Card, RoundData, TurnData}
import utils.{DoCreatePlayersEvent, DoSwitchCardEvent, TurnEndedEvent}

class ControllerSpec extends AnyWordSpec {
  "A Controller" when {
    "starts game with selected players" when {
      val c = new Controller
      val s = c.solve(new DoCreatePlayersEvent(List("PlayerA", "PlayerB")))
      "should return an SwitchCardControllerState" in {
        s.isInstanceOf[SwitchCardControllerState] should be(true)
      }
      val state = s.asInstanceOf[SwitchCardControllerState]
      "should have two players" in {
        state.players.size should be(2)
      }
      "should have 2 cardstashes with 10 cards" in {
        state.t.cardStash.size should be(2)
        state.t.cardStash.foreach(cs =>
          cs.size should be(10)
        )
      }
      "should have two empty discardedStashes" in {
        state.t.discardedStash.size should be(2)
        state.t.discardedStash.foreach(cs => cs should be(None))
      }
    }
    "switching a card" when {
      "switching with new card" when {
        val c = new Controller
        val state1 = c.solve(new DoCreatePlayersEvent(List("PlayerA", "PlayerB"))).asInstanceOf[GameRunningControllerState]
        val state2 = c.solve(new DoSwitchCardEvent(4, "new")).asInstanceOf[GameRunningControllerState]
        "open card is the new from index" in {
          state2.t.openCard should be(state1.t.cardStash(0)(4))
        }
      }
      "switching with open card" when {
        val c = new Controller
        val state1 = c.solve(new DoCreatePlayersEvent(List("PlayerA", "PlayerB"))).asInstanceOf[GameRunningControllerState]
        val state2 = c.solve(new DoSwitchCardEvent(4, "open")).asInstanceOf[GameRunningControllerState]
        "indexed and open card are switched" in {
          state2.t.openCard should be(state1.t.cardStash(0)(4))
          state2.t.cardStash(0)(4) should be(state1.t.openCard)
        }
      }

      "directly switch to next player, when player has already discarded" when {
        val state1 = new SwitchCardControllerState(
          List("PlayerA","PlayerB"),
          new RoundData(List.fill(2)(Validator.getValidator(1))),
          new TurnData(1,
            List.fill(2)(List.fill(10)(Card(8,6))),
            Card(3,7),
            List.fill(2)(None),
            List(false, true)
          )
        )

        val state2 = state1.switchCards(8, "new", new Controller)._1

        "at next asks next user to switch cards" in {
          state2.isInstanceOf[SwitchCardControllerState] should be(true)
          state2.asInstanceOf[SwitchCardControllerState].t.current_player should be(0)
        }
      }

      "discard cards" when {
        val indices = List(List(0,1,2), List(3,4,5))
        def createState(cardStash:List[List[Card]]) = new DiscardControllerState(
          List("AA", "BB"), RoundData(List.fill(2)(Validator.getValidator(1))),
          new TurnData(
            0,
            cardStash,
            Card(2,9),
            List.fill(2)(None),
            List.fill(2)(false)
          )
        )
        "discard suitable cards successfull" when {
          val state1 = createState(List(
            List(Card(1,11),Card(2,11),Card(1,11),Card(4,8),Card(2,8),Card(3,8),Card(4,2),Card(1,5),Card(4,12),Card(2,1)),
            List(Card(1,11),Card(4,7),Card(1,11),Card(4,9),Card(2,8),Card(2,5),Card(4,2),Card(1,5),Card(2,3),Card(2,1))
          ))

          val state2 = state1.discardCards(Some(indices), new Controller)._1.asInstanceOf[SwitchCardControllerState]

          "cards should have been deducted from stash" in {
            state2.t.cardStash(0).size should be(4)
          }
          "deducted cards should have been added to discardedStash" in {
            state2.t.discardedStash(0).isEmpty should be(false)
            state2.t.discardedStash(0).get.size should be(2)
            state2.t.discardedStash(0).get(0).size should be(3)
            state2.t.discardedStash(0).get(1).size should be(3)
          }
          "have switched to second player" in {
            state2.t.current_player should be(1)
          }
        }
        "discard unsuitable cards unsuccessfull" when {
          val state1 = createState(List(
            List(Card(1,11),Card(2,8),Card(1,11),Card(4,8),Card(2,8),Card(3,11),Card(4,2),Card(1,5),Card(4,12),Card(2,1)),
            List(Card(1,11),Card(4,7),Card(1,11),Card(4,9),Card(2,8),Card(2,5),Card(4,2),Card(1,5),Card(2,3),Card(2,1))
          ))

          val state2 = state1.discardCards(Some(indices), new Controller)._1.asInstanceOf[SwitchCardControllerState]
          "cardstash should be the same size" in {
            state2.t.cardStash(0).size should be(10)
          }

          "discardedStash should still be empty" in {
            state2.t.discardedStash(0) should be(None)
          }

          "have switched to second player" in {
            state2.t.current_player should be(1)
          }
        }
        "Select None to discard" when {
          val state1 = createState(List(
            List(Card(1,11),Card(2,11),Card(1,11),Card(4,8),Card(2,8),Card(3,8),Card(4,2),Card(1,5),Card(4,12),Card(2,1)),
            List(Card(1,11),Card(4,7),Card(1,11),Card(4,9),Card(2,8),Card(2,5),Card(4,2),Card(1,5),Card(2,3),Card(2,1))
          ))

          val state2 = state1.discardCards(None, new Controller)._1.asInstanceOf[SwitchCardControllerState]

          "cardstash should be the same size" in {
            state2.t.cardStash(0).size should be(10)
          }

          "discardedStash should still be empty" in {
            state2.t.discardedStash(0) should be(None)
          }

          "have switched to second player" in {
            state2.t.current_player should be(1)
          }
        }
      }
    }
  }
}
