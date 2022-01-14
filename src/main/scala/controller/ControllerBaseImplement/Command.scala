package controller.ControllerBaseImplement

import controller.*
import utils.*

class CreatePlayerCommand(players:List[String], oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = oldState.asInstanceOf[InitialState].createPlayers(players, c.asInstanceOf[Controller])
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new ProgramStartedEvent)

class SwitchCardCommand(index:Int, mode:Int, oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[SwitchCardControllerState].switchCards(index, mode, c.asInstanceOf[Controller])
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new TurnEndedEvent(oldState.asInstanceOf[SwitchCardControllerState].newCard))

class DiscardCommand(indices: List[List[Int]], oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[DiscardControllerState].discardCards(indices, c.asInstanceOf[Controller])
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToDiscardEvent)

class NoDiscardCommand(oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[DiscardControllerState].skipDiscard(c.asInstanceOf[Controller])
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToDiscardEvent)

class InjectCommand(receiving_player:Int, cardIndex:Int, stashIndex:Int, position:Int, oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[InjectControllerState].injectCard(receiving_player, cardIndex, stashIndex, position, c.asInstanceOf[Controller])
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToInjectEvent)

class NoInjectCommand(oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[InjectControllerState].skipInject(c.asInstanceOf[Controller])
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToInjectEvent)