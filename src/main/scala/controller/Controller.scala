package controller

import model.{Card, RoundData, TurnData}
import utils.{GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, Observable, OutputEvent, TurnEndedEvent, Utils}
import Utils.{INJECT_AFTER, INJECT_TO_FRONT, NEW_CARD, OPENCARD, randomColor, randomValue}

import scala.util.Random

class Controller extends Observable:
  val undoManager = new UndoManager

  def createCard: Card = Card(randomColor + 1, randomValue + 1)
  def createCardStash(numberOfPlayers: Int): List[List[Card]] = List.fill(numberOfPlayers)(createCheat)
  def nextPlayer(currentPlayer: Int, numberOfPlayers: Int): Int = (currentPlayer + 1) % numberOfPlayers
  def createInitialTurnData(numberOfPlayers:Int) = new TurnData(
    0,
    createCardStash(numberOfPlayers),
    createCard,
    List.fill(numberOfPlayers)(None:Option[List[List[Card]]]),
    List.fill(numberOfPlayers)(false))

  def createCheat = List(Card(1,11),Card(2,11),Card(4,11),Card(3,7),Card(1,7),Card(4,7), createCard, createCard, createCard, createCard)
  
  var state:ControllerState = new InitialState
  
  def getState = state
  
  def getGameData: (RoundData, TurnData) =
    val s = state.asInstanceOf[GameRunningControllerState]
    (s.r, s.t)

  def getPlayers(): List[String] = state.asInstanceOf[GameRunningControllerState].players

  def solve(c: Command):ControllerState =
    val res = undoManager.doStep(c, this)
    state = res._1
    notifyObservers(res._2)
    state

  def undo:ControllerState =
    val res = undoManager.undoStep(this)
    state = res._1
    notifyObservers(res._2)
    state


trait ControllerState

class InitialState extends ControllerState:
  def createPlayers(pPlayers: List[String], c:Controller): (ControllerState, OutputEvent) =
    def numberOfPlayers = pPlayers.size
    (new SwitchCardControllerState(pPlayers,
      new RoundData(List.fill(numberOfPlayers)(Validator.getValidator(1))),
      c.createInitialTurnData(numberOfPlayers)),
      new TurnEndedEvent)

class GameRunningControllerState(val players: List[String], val r:RoundData, val t:TurnData) extends ControllerState:
  def currentPlayer = t.current_player


class SwitchCardControllerState(players: List[String], r:RoundData, t:TurnData) extends GameRunningControllerState(players, r, t):
  def switchCards(index: Int, mode: Int, c:Controller):(GameRunningControllerState, OutputEvent) =
    def newOpenCard = t.cardStash(currentPlayer)(index)
    def newPlayerCardStash =
      if (mode == NEW_CARD) t.cardStash(currentPlayer).updated(index, c.createCard)
      else if(mode == OPENCARD) t.cardStash(currentPlayer).updated(index, t.openCard)
      else throw new IllegalArgumentException

    def newStash = t.cardStash.updated(currentPlayer, newPlayerCardStash)

    if(t.player_has_discarded(currentPlayer))
      def newTurnData = new TurnData(t.current_player, newStash, newOpenCard, t.discardedStash, t.player_has_discarded)
      (new InjectState(players, r, newTurnData), new GoToInjectEvent)
    else
      def newTurnData = new TurnData(t.current_player, newStash, newOpenCard, t.discardedStash, t.player_has_discarded)
      (new DiscardControllerState(players, r, newTurnData), new GoToDiscardEvent)


class DiscardControllerState(players: List[String], r:RoundData, t:TurnData) extends GameRunningControllerState(players, r, t):
  private def nextPlayerOnly(c:Controller) = new TurnData(c.nextPlayer(t.current_player, players.size), t.cardStash, t.openCard, t.discardedStash, t.player_has_discarded)
  def discardCards(cardIndices: List[List[Int]], c:Controller): (GameRunningControllerState, OutputEvent) =
      def newTurnData =
        if (r.validators(currentPlayer).validate(t.cardStash(currentPlayer), cardIndices))
          def playerCards = t.cardStash(currentPlayer)
          def sublist_newCardstash = Utils.inverseIndexList(cardIndices.flatten, t.cardStash(currentPlayer).size).map(n => playerCards(n))
          def sublist_newDiscardedCards = cardIndices.map(n => n.map(n2 => t.cardStash(currentPlayer)(n2)))
          def newCardStash = t.cardStash.updated(currentPlayer, sublist_newCardstash)
          def newDiscardedStash = t.discardedStash.updated(currentPlayer, Some(sublist_newDiscardedCards))
          new TurnData(c.nextPlayer(currentPlayer, players.size), newCardStash, t.openCard, newDiscardedStash, t.player_has_discarded.updated(currentPlayer, true))
        else
          nextPlayerOnly(c)
      (new SwitchCardControllerState(players, r, newTurnData), new TurnEndedEvent)

  def skipDiscard(c:Controller) = (new SwitchCardControllerState(players, r, nextPlayerOnly(c)), new TurnEndedEvent)

class InjectState (players: List[String], r:RoundData, t:TurnData) extends GameRunningControllerState(players, r, t):
  private def newTurnDataNextPlayer(c:Controller) = new TurnData(c.nextPlayer(t.current_player, players.size), t.cardStash, t.openCard, t.discardedStash, t.player_has_discarded)
  def injectCard(receiving_player:Int, cardIndex:Int, stashIndex:Int, position:Int, c:Controller): (GameRunningControllerState, OutputEvent) =
      def targetStash = t.discardedStash(receiving_player).get
      def discardedSubStash = targetStash(stashIndex)

      def cardToInject = t.cardStash(currentPlayer)(cardIndex)

      def canAppend = (t.player_has_discarded(receiving_player) &&
        r.validators(receiving_player).canAppend(discardedSubStash, cardToInject, stashIndex, position))

      if(!canAppend)
        return (new SwitchCardControllerState(players, r, newTurnDataNextPlayer(c)), new TurnEndedEvent)

      def newSublistCardStash = t.cardStash(currentPlayer).drop(cardIndex)
      def newCardStash = t.cardStash.updated(currentPlayer, newSublistCardStash)

      def newDiscardedStashSublist =
        if (position == INJECT_TO_FRONT)
          cardToInject :: discardedSubStash
        else if (position == INJECT_AFTER)
          (cardToInject :: (discardedSubStash).reverse).reverse
        else
          throw new IllegalArgumentException

      def newDiscardedStash = t.discardedStash.updated(receiving_player, Some(targetStash.updated(stashIndex, newDiscardedStashSublist)))

      def newTurnData = new TurnData(t.current_player, newCardStash, t.openCard, newDiscardedStash, t.player_has_discarded)
      return (new InjectState(players, r, newTurnData), new GoToInjectEvent)
      
  def skipInject(c:Controller) = (new SwitchCardControllerState(players, r, newTurnDataNextPlayer(c)), new TurnEndedEvent)