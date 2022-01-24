package controller.ControllerBaseImplement

import model.{Card, RoundData, TurnData}
import utils.{DoCreatePlayerEvent, DoDiscardEvent, DoInjectEvent, DoNoDiscardEvent, DoNoInjectEvent, DoSwitchCardEvent, GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, InputEvent, NewRoundEvent, Observable, OutputEvent, ProgramStartedEvent, TurnEndedEvent, Utils}
import Utils.{INJECT_AFTER, INJECT_TO_FRONT, NEW_CARD, OPENCARD, randomColor, randomValue}
import controller.{ControllerInterface, ControllerStateInterface, GameRunningControllerStateInterface, UndoManager}
import scalafx.application.Platform
import com.google.inject.Inject
import com.google.inject.name.Names
import com.google.inject.{Guice, Inject}
import controller.ValidatorFactoryInterface
import controller.ValidatorBaseImplement.ValidatorFactory
import com.google.inject.Guice
import model.FileIoInterface
import model.JsonImplement.FileIoJson
import scala.Phase10Module

import scala.util.Random

class Controller @Inject() extends ControllerInterface:
  val validatorFactory = Guice.createInjector(new Phase10Module).getInstance(classOf[ValidatorFactoryInterface])
  val fileIO = Guice.createInjector(new Phase10Module).getInstance(classOf[FileIoInterface])
  private val undoManager = new UndoManager[Controller]

  def createCard: Card = Card(randomColor + 1, randomValue + 1)
  def createCardStash(numberOfPlayers: Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))
  def nextPlayer(currentPlayer: Int, numberOfPlayers: Int): Int = (currentPlayer + 1) % numberOfPlayers
  def createInitialTurnData(numberOfPlayers:Int) = new TurnData(
    0,
    createCardStash(numberOfPlayers),
    createCard,
    List.fill(numberOfPlayers)(None:Option[List[List[Card]]]))

  def createNewRound(r:RoundData, cardStashes: List[List[Card]], discarded:List[Boolean]):RoundData =
    def updateValidators = r.validators.indices.map { idx =>
      if(discarded(idx))
        validatorFactory.getValidator(r.validators(idx).getNumberOfPhase() + 1)
      else
        r.validators(idx)
    }.toList
    def countErrorpoints = r.errorPoints.indices.map(idx =>
      r.errorPoints(idx) + cardStashes(idx).map(c =>
        c.errorPoints).sum).toList
    new RoundData(updateValidators, countErrorpoints)

  def createCheat = List(Card(1,11),Card(2,11),Card(4,11),Card(3,7),Card(1,7),Card(4,7), createCard, createCard, createCard, createCard)

  def getInitialState():ControllerStateInterface = new InitialState(validatorFactory)

  private var state:ControllerStateInterface = getInitialState()
  def getState = state
  
  def getGameData: (RoundData, TurnData) =
    val s = state.asInstanceOf[GameRunningControllerStateInterface]
    (s.r, s.t)

  def getPlayers(): List[String] = state.asInstanceOf[GameRunningControllerStateInterface].players

  def solve(e: InputEvent, executePlatform_runLater:Boolean = true):ControllerStateInterface =
    val command = e match {
      case e1: DoCreatePlayerEvent => new CreatePlayerCommand(e1.players, state)
      case e2: DoSwitchCardEvent => new SwitchCardCommand(e2.index, e2.mode, state)
      case e3: DoDiscardEvent => new DiscardCommand(e3.indices, state)
      case e4: DoNoDiscardEvent => new NoDiscardCommand(state)
      case e5: DoInjectEvent => new InjectCommand(e5.receiving_player, e5.cardIndex, e5.stashIndex, e5.position, state)
      case e6: DoNoInjectEvent => new NoInjectCommand(state)
    }
    val res = undoManager.doStep(command, this)
    state = res._1
    if(executePlatform_runLater)
      Platform.runLater(() => notifyObservers(res._2))
    else
      //Platform.runLater causes error in tests
      notifyObservers(res._2)
    state

  def undo:ControllerStateInterface =
    val res = undoManager.undoStep(this)
    state = res._1
    Platform.runLater(() => notifyObservers(res._2))
    state

  def save: Unit = fileIO.save(state.asInstanceOf[GameRunningControllerStateInterface])


class InitialState(validator: ValidatorFactoryInterface) extends ControllerStateInterface:
  def createPlayers(pPlayers: List[String], controller:Controller): (ControllerStateInterface, OutputEvent) =
    def numberOfPlayers = pPlayers.size
    val newCard = controller.createCard
    (new SwitchCardControllerState(pPlayers,
      new RoundData(List.fill(numberOfPlayers)(validator.getValidator(1)), List.fill(numberOfPlayers)(0)),
      controller.createInitialTurnData(numberOfPlayers),
      newCard),
      new GameStartedEvent(newCard))


class SwitchCardControllerState(pPlayers: List[String], pR:RoundData, pT:TurnData, pNewCard:Card) extends GameRunningControllerStateInterface:
  override val players: List[String] = pPlayers
  override val r: RoundData = pR
  override val t: TurnData = pT
  val newCard: Card = pNewCard

  def switchCards(index: Int, mode: Int, controller:Controller):(GameRunningControllerStateInterface, OutputEvent) =
    def newOpenCard = t.cardStash(currentPlayer)(index)
    def newPlayerCardStash =
      if (mode == NEW_CARD) t.cardStash(currentPlayer).updated(index, newCard)
      else if(mode == OPENCARD) t.cardStash(currentPlayer).updated(index, t.openCard)
      else throw new IllegalArgumentException

    def newStash = t.cardStash.updated(currentPlayer, newPlayerCardStash)

    if(t.discardedStash(currentPlayer).nonEmpty)
      def newTurnData = new TurnData(t.current_player, newStash, newOpenCard, t.discardedStash)
      (new InjectControllerState(players, r, newTurnData), new GoToInjectEvent)
    else
      def newTurnData = new TurnData(t.current_player, newStash, newOpenCard, t.discardedStash)
      (new DiscardControllerState(players, r, newTurnData), new GoToDiscardEvent)


class DiscardControllerState(pPlayers: List[String], pR:RoundData, pT:TurnData) extends GameRunningControllerStateInterface:
  override val players: List[String] = pPlayers
  override val r: RoundData = pR
  override val t: TurnData = pT

  private def nextPlayerOnly(c:Controller) = new TurnData(c.nextPlayer(t.current_player, players.size), t.cardStash, t.openCard, t.discardedStash)

  def discardCards(cardIndices: List[List[Int]], controller:Controller): (GameRunningControllerStateInterface, OutputEvent) =
    def newTurnData:TurnData =
      if (r.validators(currentPlayer).validate(t.cardStash(currentPlayer), cardIndices))
        def playerCards = t.cardStash(currentPlayer)
        def sublist_newCardstash = Utils.inverseIndexList(cardIndices.flatten, t.cardStash(currentPlayer).size).map(n => playerCards(n))
        def sublist_newDiscardedCards = cardIndices.map(n => n.map(n2 => t.cardStash(currentPlayer)(n2)))
        def newCardStash = t.cardStash.updated(currentPlayer, sublist_newCardstash)
        def newDiscardedStash = t.discardedStash.updated(currentPlayer, Some(sublist_newDiscardedCards))
        new TurnData(controller.nextPlayer(currentPlayer, players.size), newCardStash, t.openCard, newDiscardedStash)
      else
        nextPlayerOnly(controller)
    val newCard = controller.createCard
    (new SwitchCardControllerState(players, r, newTurnData, newCard), new TurnEndedEvent(newCard))

  def skipDiscard(controller:Controller) =
    val newCard = controller.createCard
    (new SwitchCardControllerState(players, r, nextPlayerOnly(controller), newCard), new TurnEndedEvent(newCard))

class InjectControllerState(pPlayers: List[String], pR:RoundData, pT:TurnData) extends GameRunningControllerStateInterface:
  override val players: List[String] = pPlayers
  override val r: RoundData = pR
  override val t: TurnData = pT

  private def newTurnDataNextPlayer(c:Controller) = new TurnData(c.nextPlayer(t.current_player, players.size), t.cardStash, t.openCard, t.discardedStash)

  def injectCard(receiving_player:Int, cardIndex:Int, stashIndex:Int, position:Int, controller:Controller): (GameRunningControllerStateInterface, OutputEvent) =
    def targetStash = t.discardedStash(receiving_player).get
      def discardedSubStash = targetStash(stashIndex)
      def cardToInject = t.cardStash(currentPlayer)(cardIndex)

      def canAppend = (t.discardedStash(receiving_player).nonEmpty &&
        r.validators(receiving_player).canAppend(discardedSubStash, cardToInject, stashIndex, position))

      if(canAppend)
        if(t.cardStash(currentPlayer).size == 1)
          val newCard = controller.createCard
          return (new SwitchCardControllerState(
            players,
            controller.createNewRound(r, t.cardStash.updated(currentPlayer, Nil), t.discardedStash.map(s => s.nonEmpty)),
            controller.createInitialTurnData(players.size),
            newCard), new NewRoundEvent(newCard))

        def newSublistCardStash = t.cardStash(currentPlayer).patch(cardIndex, Nil, 1)
        def newCardStash = t.cardStash.updated(currentPlayer, newSublistCardStash)

        def newDiscardedStashSublist =
          if (position == INJECT_TO_FRONT)
            cardToInject :: discardedSubStash
          else if (position == INJECT_AFTER)
            (cardToInject :: (discardedSubStash).reverse).reverse
          else
            throw new IllegalArgumentException

        def newDiscardedStash = t.discardedStash.updated(receiving_player, Some(targetStash.updated(stashIndex, newDiscardedStashSublist)))

        def newTurnData = new TurnData(t.current_player, newCardStash, t.openCard, newDiscardedStash)
        (new InjectControllerState(players, r, newTurnData), new GoToInjectEvent)
      else
        val newCard = controller.createCard
        return (new SwitchCardControllerState(players, r, newTurnDataNextPlayer(controller), newCard), new TurnEndedEvent(newCard))
      
  def skipInject(controller:Controller) =
    val newCard = controller.createCard
    (new SwitchCardControllerState(players, r, newTurnDataNextPlayer(controller), newCard), new TurnEndedEvent(newCard))