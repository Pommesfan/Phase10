package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import model.{Card, RoundData, TurnData}
import utils.{TurnEndedEvent, Utils}
import Utils.{INJECT_TO_FRONT, INJECT_AFTER}
import utils.Utils
import Utils.{NEW_CARD, OPENCARD}
import GroupType.{MULTIPLES,SEQUENCE,SAME_COLOR}

class ControllerSpec extends AnyWordSpec {
  "A Controller" when {
    "starts game with selected players" when {
      val c = new Controller
      val s = c.solve(new CreatePlayerCommand(List("PlayerA", "PlayerB"), c.state))
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
        val state1 = c.solve(new CreatePlayerCommand(List("PlayerA", "PlayerB"), c.state)).asInstanceOf[GameRunningControllerState]
        val state2 = c.solve(new SwitchCardCommand(4, NEW_CARD, state1)).asInstanceOf[GameRunningControllerState]
        "open card is the new from index" in {
          state2.t.openCard should be(state1.t.cardStash(0)(4))
        }
      }
      "switching with open card" when {
        val c = new Controller
        val state1 = c.solve(new CreatePlayerCommand(List("PlayerA", "PlayerB"), c.state)).asInstanceOf[GameRunningControllerState]
        val state2 = c.solve(new SwitchCardCommand(4, OPENCARD, state1)).asInstanceOf[GameRunningControllerState]
        "indexed and open card are switched" in {
          state2.t.openCard should be(state1.t.cardStash(0)(4))
          state2.t.cardStash(0)(4) should be(state1.t.openCard)
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
            List.fill(2)(None)
          )
        )
        "discard suitable cards successfull" when {
          val state1 = createState(List(
            List(Card(1,11),Card(2,11),Card(1,11),Card(4,8),Card(2,8),Card(3,8),Card(4,2),Card(1,5),Card(4,12),Card(2,1)),
            List(Card(1,11),Card(4,7),Card(1,11),Card(4,9),Card(2,8),Card(2,5),Card(4,2),Card(1,5),Card(2,3),Card(2,1))
          ))

          val state2 = state1.discardCards(indices, new Controller)._1.asInstanceOf[SwitchCardControllerState]

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

          val state2 = state1.discardCards(indices, new Controller)._1.asInstanceOf[SwitchCardControllerState]
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

          val state2 = state1.skipDiscard(new Controller)._1.asInstanceOf[SwitchCardControllerState]

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
    "Inject card to player itself or another if he has already discarded and cards fit to discardedStash" when {
      def createState(cardStash:List[List[Card]], discardedStash:List[Option[List[List[Card]]]], currentPlayer:Int) = new InjectState(
        List("PlayerA", "PlayerB"), new RoundData(List.fill(2)(Validator.getValidator(1))),
        new TurnData(currentPlayer, cardStash, Card(2,5), discardedStash)
      )

      "process without discarding None" when {
        val stash = List(
          List(),
          List(Card(2,3),Card(3,8),Card(4,1),Card(2,9))
        )
        val discardedStash = List(
          None,
          Some(List(List(Card(1,11),Card(3,11),Card(4,11)), List(Card(3,5),Card(4,5),Card(1,5))))
        )
        val state1 = createState(stash, discardedStash, 1)
        val state2 = state1.skipInject(new Controller)._1.asInstanceOf[SwitchCardControllerState]
        def t1 = state1.t
        def t2 = state2.t
        "change to next player" in {
          state2.t.current_player should be(0)
        }
        "other turndata haven´t changed" in {
          t1.cardStash should be(t2.cardStash)
          t1.openCard should be(t2.openCard)
          t1.discardedStash should be(t2.discardedStash)
        }
      }
      "process with fitting card" when {
        val stash = List(
          List(Card(2,4),Card(3,8),Card(1,10),Card(2,11)),
          List(Card(2,3),Card(3,5),Card(4,1),Card(2,9))
        )
        val discardedStash = List(
          Some(List(List(Card(3,5),Card(2,5),Card(1,5)),List(Card(4,12),Card(3,12),Card(1,12)))),
          Some(List(List(Card(3,8),Card(4,8),Card(1,8)),List(Card(1,11),Card(3,11),Card(4,11))))
        )

        val receiving_player = 0
        val cardIndex = 1
        val stashIndex = 0
        val position = INJECT_TO_FRONT
        val currentplayer = 1

        val state1 = createState(stash, discardedStash, currentplayer)
        val newState = state1.injectCard(0, cardIndex, stashIndex, position, new Controller)._1
        "should have success" in {
          newState.isInstanceOf[InjectState] should be(true)
        }
        val state2 = newState.asInstanceOf[InjectState]

        val t1 = state1.t
        val t2 = state2.t
        "should have injected" when {
          "stashes should have changed it´s size" in {
            t2.cardStash(currentplayer).size should be(t1.cardStash(currentplayer).size - 1)
            t2.discardedStash(receiving_player).get(stashIndex).size should be(t1.discardedStash(receiving_player).get(stashIndex).size + 1)
          }
        }
      }
    }
  }
}
