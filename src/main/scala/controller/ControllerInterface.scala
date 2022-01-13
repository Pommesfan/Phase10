package controller

import controller.ControllerBaseImplement.Controller
import model.{Card, RoundData, TurnData}
import scalafx.application.Platform
import utils.Utils.{randomColor, randomValue}
import utils.{GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, NewRoundEvent, Observable, OutputEvent, TurnEndedEvent, Utils}

trait ControllerInterface extends Observable:
  def getState:ControllerStateInterface

  def getGameData: (RoundData, TurnData)

  def getInitialState():ControllerStateInterface

  def getPlayers(): List[String]

  def solve(c: Command):ControllerStateInterface

  def undo:ControllerStateInterface
  
trait ControllerStateInterface

trait InitialStateInterface extends ControllerStateInterface:
  def createPlayers(pPlayers: List[String], c:ControllerInterface): (ControllerStateInterface, OutputEvent)

trait GameRunningControllerStateInterface extends ControllerStateInterface:
  val players: List[String]
  val r:RoundData
  val t:TurnData

  def currentPlayer = t.current_player


trait SwitchCardControllerStateInterface extends GameRunningControllerStateInterface:
  val newCard:Card

  def switchCards(index: Int, mode: Int, c:ControllerInterface):(GameRunningControllerStateInterface, OutputEvent)


trait DiscardControllerStateInterface extends GameRunningControllerStateInterface:
  def discardCards(cardIndices: List[List[Int]], c:ControllerInterface): (GameRunningControllerStateInterface, OutputEvent)
  def skipDiscard(c:ControllerInterface): (GameRunningControllerStateInterface, OutputEvent)

trait InjectControllerStateInterface extends GameRunningControllerStateInterface:
  def injectCard(receiving_player:Int, cardIndex:Int, stashIndex:Int, position:Int, c:ControllerInterface): (GameRunningControllerStateInterface, OutputEvent)
  def skipInject(c:ControllerInterface): (GameRunningControllerStateInterface, OutputEvent)