package controller.ControllerBaseImplement

import controller.{GameRunningControllerStateInterface, ValidatorFactoryInterface}
import controller.ValidatorBaseImplement.ValidatorFactory
import model.{Card, DiscardedCardDeck, PlayerCardDeck, RoundData, TurnData}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import utils.Utils.{INJECT_AFTER, INJECT_TO_FRONT, NEW_CARD, OPENCARD}
import utils.{DoCreatePlayerEvent, DoSwitchCardEvent, TurnEndedEvent, Utils}

class ControllerSpec extends AnyWordSpec {
  "A Controller" when {
    val validatorFactory: ValidatorFactoryInterface = new ValidatorFactory
    "starts game with selected players" when {
      val c = new Controller
      val s = c.solve(new DoCreatePlayerEvent(List("PlayerA", "PlayerB")), executePlatform_runLater = false)
      "should return an SwitchCardControllerState" in {
        s.isInstanceOf[SwitchCardControllerState] should be(true)
      }
      val state = s.asInstanceOf[SwitchCardControllerState]
      "should have two players" in {
        state.players.size should be(2)
      }
      "should have 2 cardstashes with 10 cards" in {
        state.t.playerCardDeck.cards.size should be(2)
        state.t.playerCardDeck.cards.foreach(cs =>
          cs.size should be(10)
        )
      }
      "should have two empty discardedStashes" in {
        state.t.discardedCardDeck.cards.size should be(2)
        state.t.discardedCardDeck.cards.foreach(cs => cs should be(None))
      }
    }
    "switching a card" when {
      "switching with new card" when {
        val c = new Controller
        val state1 = c.solve(new DoCreatePlayerEvent(List("PlayerA", "PlayerB")), false).asInstanceOf[GameRunningControllerStateInterface]
        val state2 = c.solve(new DoSwitchCardEvent(4, NEW_CARD), false).asInstanceOf[GameRunningControllerStateInterface]
        "open card is the new from index" in {
          state2.t.openCard should be(state1.t.playerCardDeck.cards(0)(4))
        }
      }
      "switching with open card" when {
        val c = new Controller
        val state1 = c.solve(new DoCreatePlayerEvent(List("PlayerA", "PlayerB")), false).asInstanceOf[GameRunningControllerStateInterface]
        val state2 = c.solve(new DoSwitchCardEvent(4, OPENCARD), false).asInstanceOf[GameRunningControllerStateInterface]
        "indexed and open card are switched" in {
          state2.t.openCard should be(state1.t.playerCardDeck.cards(0)(4))
          state2.t.playerCardDeck.cards(0)(4) should be(state1.t.openCard)
        }
      }


      "discard cards" when {
        val indices = List(List(0,1,2), List(3,4,5))
        def createState(cardStash:List[List[Card]]) = new DiscardControllerState(
          List("AA", "BB"), RoundData(List.fill(2)(validatorFactory.getValidator(1)), List.fill(2)(0)),
          new TurnData(
            0,
            new PlayerCardDeck(cardStash),
            Card(2,9),
            new DiscardedCardDeck(List.fill(2)(None))
          )
        )
        "discard suitable cards successfull" when {
          val state1 = createState(List(
            List(Card(1,11),Card(2,11),Card(1,11),Card(4,8),Card(2,8),Card(3,8),Card(4,2),Card(1,5),Card(4,12),Card(2,1)),
            List(Card(1,11),Card(4,7),Card(1,11),Card(4,9),Card(2,8),Card(2,5),Card(4,2),Card(1,5),Card(2,3),Card(2,1))
          ))

          val state2 = state1.discardCards(indices, new Controller)._1.asInstanceOf[SwitchCardControllerState]

          "cards should have been deducted from stash" in {
            state2.t.playerCardDeck.cards(0).size should be(4)
          }
          "deducted cards should have been added to discardedStash" in {
            state2.t.discardedCardDeck.cards(0).isEmpty should be(false)
            state2.t.discardedCardDeck.cards(0).get.size should be(2)
            state2.t.discardedCardDeck.cards(0).get(0).size should be(3)
            state2.t.discardedCardDeck.cards(0).get(1).size should be(3)
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
            state2.t.playerCardDeck.cards(0).size should be(10)
          }

          "discardedStash should still be empty" in {
            state2.t.discardedCardDeck.cards(0) should be(None)
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
            state2.t.playerCardDeck.cards(0).size should be(10)
          }

          "discardedStash should still be empty" in {
            state2.t.discardedCardDeck.cards(0) should be(None)
          }

          "have switched to second player" in {
            state2.t.current_player should be(1)
          }
        }
      }
    }
    "Inject card to player itself or another if he has already discarded and cards fit to discardedStash" when {
      def createState(cardStash:List[List[Card]], discardedStash:List[Option[List[List[Card]]]], currentPlayer:Int) = new InjectControllerState(
        List("PlayerA", "PlayerB"), new RoundData(List.fill(2)(validatorFactory.getValidator(1)), List.fill(2)(0)),
        new TurnData(currentPlayer, new PlayerCardDeck(cardStash), Card(2,5), new DiscardedCardDeck(discardedStash))
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
          t1.playerCardDeck.cards should be(t2.playerCardDeck.cards)
          t1.openCard should be(t2.openCard)
          t1.discardedCardDeck.cards should be(t2.discardedCardDeck.cards)
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
          newState.isInstanceOf[InjectControllerState] should be(true)
        }
        val state2 = newState.asInstanceOf[InjectControllerState]

        val t1 = state1.t
        val t2 = state2.t
        "should have injected" when {
          "stashes should have changed it´s size" in {
            t2.playerCardDeck.cards(currentplayer).size should be(t1.playerCardDeck.cards(currentplayer).size - 1)
            t2.discardedCardDeck.cards(receiving_player).get(stashIndex).size should be(t1.discardedCardDeck.cards(receiving_player).get(stashIndex).size + 1)
          }
        }
      }
      "When inject card with only one left, end round" when {
        val c = new Controller
        val playerCardDeck = new PlayerCardDeck(List(
          List(Card(2,8)),
          List(Card(2,3),Card(3,5),Card(4,1),Card(2,9)))
        )
        val discardedCardDeck = new DiscardedCardDeck(List(
          Some(List(List(Card(3,8),Card(4,8),Card(1,8)),List(Card(1,11),Card(3,11),Card(4,11)))),
          Some(List(List(Card(3,8),Card(4,8),Card(1,8)),List(Card(1,11),Card(3,11),Card(4,11)))))
        )
        val state1 = new InjectControllerState(
          List("PlayerA", "PlayerB"),
          RoundData(List.fill(2)(validatorFactory.getValidator(1)), List.fill(2)(0)),
          TurnData(0, playerCardDeck, c.createCard, discardedCardDeck)
        )

        val state2 = state1.injectCard(1, 0, 0, INJECT_TO_FRONT, c)._1
        "state should be GameRunningControllerState" in {
          state2.isInstanceOf[GameRunningControllerStateInterface] should be(true)
        }
        val state3 = state2.asInstanceOf[GameRunningControllerStateInterface]
        val t2 = state3.t

        "player have discarded and get in phase 2" in {
          state3.r.validators.foreach(v => v.getNumberOfPhase() should be(2))
        }

        "have cardstashes of 10 and empty discardedStashes" in {
          t2.playerCardDeck.cards.foreach(c => c.size should be(10))
          t2.discardedCardDeck.cards.foreach(c => c should be(None))
        }
      }
    }
  }
}
