package controller

import model.{Card, RoundData, TurnData}
import utils.{CardSwitchedEvent, DoCreatePlayersEvent, DoDiscardEvent, DoSwitchCardEvent, GameStartedEvent, InputEvent, Observable, OutputEvent, TurnEndedEvent, Utils}
import Utils.{randomColor, randomValue}
import scala.util.Random

class Controller extends Observable:
  def createCard: Card = Card(randomColor + 1, randomValue + 1)
  def createCardStash(numberOfPlayers: Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))
  def nextPlayer(currentPlayer: Int, numberOfPlayers: Int): Int = (currentPlayer + 1) % numberOfPlayers
  def createCheat = List(Card(1,11),Card(2,11),Card(4,11),Card(3,7),Card(1,7),Card(4,7), createCard, createCard, createCard, createCard)

  private var state:ControllerState = new InitialState
  
  def getState = state

  def getGameData: (RoundData, TurnData) =
    val s = state.asInstanceOf[GameRunningControllerState]
    (s.r, s.t)

  def getPlayers(): List[String] = state.asInstanceOf[GameRunningControllerState].players

  def solve(e:InputEvent):ControllerState =
    val (newState:ControllerState, event:OutputEvent) = e match
      case e1:DoCreatePlayersEvent => state.asInstanceOf[InitialState].createPlayers(e1.players, this)
      case e2:DoSwitchCardEvent => state.asInstanceOf[SwitchCardControllerState].switchCards(e2.index, e2.mode, this)
      case e3:DoDiscardEvent => state.asInstanceOf[DiscardControllerState].discardCards(e3.indices, this)
    state = newState
    notifyObservers(event)
    state


trait ControllerState

class InitialState extends ControllerState:
  def createPlayers(pPlayers: List[String], c:Controller): (ControllerState, OutputEvent) =
    def numberOfPlayers = pPlayers.size
    (new SwitchCardControllerState(pPlayers,
      new RoundData(List.fill(numberOfPlayers)(Validator.getValidator(1))),
      new TurnData(
        0,
        c.createCardStash(numberOfPlayers),
        c.createCard,
        List.fill(numberOfPlayers)(None:Option[List[Card]]),
        List.fill(numberOfPlayers)(false))),
      new TurnEndedEvent)

class GameRunningControllerState(val players: List[String], val r:RoundData, val t:TurnData) extends ControllerState:
  def currentPlayer = t.current_player


class SwitchCardControllerState(players: List[String], r:RoundData, t:TurnData) extends GameRunningControllerState(players, r, t):
  def switchCards(index: Int, mode: String, c:Controller):(GameRunningControllerState, OutputEvent) =
    def newOpernCard = t.cardStash(currentPlayer)(index)
    def newPlayerCardStash =
      if (mode == "new") t.cardStash(currentPlayer).updated(index, c.createCard)
      else if(mode == "open") t.cardStash(currentPlayer).updated(index, t.openCard)
      else throw new IllegalArgumentException

    def newStash = t.cardStash.updated(currentPlayer, newPlayerCardStash)

    if(t.player_has_discarded(currentPlayer))
      def newTurnData = new TurnData(c.nextPlayer(t.current_player, players.size), newStash, newOpernCard, t.discardedStash, t.player_has_discarded)
      (new SwitchCardControllerState(players, r, newTurnData), new TurnEndedEvent)
    else
      def newTurnData = new TurnData(t.current_player, newStash, newOpernCard, t.discardedStash, t.player_has_discarded)
      (new DiscardControllerState(players, r, newTurnData), new CardSwitchedEvent)


class DiscardControllerState(players: List[String], r:RoundData, t:TurnData) extends GameRunningControllerState(players, r, t):
  def discardCards(indices: Option[List[Int]], c:Controller): (GameRunningControllerState, OutputEvent) =
      def newTurnData =
        if (indices.nonEmpty && r.validators(currentPlayer).validate(t.cardStash(currentPlayer), indices.get))
          def cardIndices = indices.get
          def playerCards = t.cardStash(currentPlayer)
          def sublist_newCardstash = Utils.inverseIndexList(cardIndices, t.cardStash(currentPlayer).size).map(n => playerCards(n))
          def sublist_newDiscardedCards = cardIndices.map(n => t.cardStash(currentPlayer)(n))
          def newCardStash = t.cardStash.updated(currentPlayer, sublist_newCardstash)
          def newDiscardedStash = t.discardedStash.updated(currentPlayer, Some(sublist_newDiscardedCards))
          new TurnData(c.nextPlayer(currentPlayer, players.size), newCardStash, t.openCard, newDiscardedStash, t.player_has_discarded.updated(currentPlayer, true))
        else
          new TurnData(c.nextPlayer(t.current_player, players.size), t.cardStash, t.openCard, t.discardedStash, t.player_has_discarded)
      (new SwitchCardControllerState(players, r, newTurnData), new TurnEndedEvent)
