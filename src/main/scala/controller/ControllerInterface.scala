package controller

import controller.ControllerBaseImplement.Controller
import controller.Command
import model.{Card, RoundData, TurnData}
import scalafx.application.Platform
import utils.Utils.{randomColor, randomValue}
import utils.{GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, InputEvent, NewRoundEvent, Observable, OutputEvent, TurnEndedEvent, Utils}

trait ControllerInterface extends Observable:
  def getState:ControllerStateInterface

  def getGameData: (RoundData, TurnData)

  def getInitialState():ControllerStateInterface

  def getPlayers(): List[String]

  def solve(e: InputEvent, executePlatform_runLater:Boolean = true):ControllerStateInterface

  def undo:ControllerStateInterface


trait ControllerStateInterface


trait GameRunningControllerStateInterface extends ControllerStateInterface:
  val players: List[String]
  val r:RoundData
  val t:TurnData

  def currentPlayer = t.current_player
