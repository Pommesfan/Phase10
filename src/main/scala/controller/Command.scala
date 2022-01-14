package controller

import utils.{ProgramStartedEvent, GoToDiscardEvent, GoToInjectEvent, OutputEvent, TurnEndedEvent}

trait Command:
  def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent)
  def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent)

class CreatePlayerCommand(players:List[String], oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = oldState.asInstanceOf[InitialStateInterface].createPlayers(players, c)
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new ProgramStartedEvent)

class SwitchCardCommand(index:Int, mode:Int, oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[SwitchCardControllerStateInterface].switchCards(index, mode, c)
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new TurnEndedEvent(oldState.asInstanceOf[SwitchCardControllerStateInterface].newCard))

class DiscardCommand(indices: List[List[Int]], oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[DiscardControllerStateInterface].discardCards(indices, c)
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToDiscardEvent)

class NoDiscardCommand(oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[DiscardControllerStateInterface].skipDiscard(c)
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToDiscardEvent)

class InjectCommand(receiving_player:Int, cardIndex:Int, stashIndex:Int, position:Int, oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[InjectControllerStateInterface].injectCard(receiving_player, cardIndex, stashIndex, position, c)
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToInjectEvent)

class NoInjectCommand(oldState: ControllerStateInterface) extends Command:
  override def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =   oldState.asInstanceOf[InjectControllerStateInterface].skipInject(c)
  override def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) = (oldState, new GoToInjectEvent)