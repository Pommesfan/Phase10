package controller

import utils.{GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, OutputEvent, TurnEndedEvent}

trait Command:
  def doStep(c:Controller):(ControllerState, OutputEvent)
  def undoStep(c:Controller):(ControllerState, OutputEvent)

class CreatePlayerCommand(players:List[String], oldState: ControllerState) extends Command:
  override def doStep(c:Controller):(ControllerState, OutputEvent) = oldState.asInstanceOf[InitialState].createPlayers(players, c)
  override def undoStep(c:Controller):(ControllerState, OutputEvent) = (oldState, new GameStartedEvent)

class SwitchCardCommand(index:Int, mode:Int, oldState: ControllerState) extends Command:
  override def doStep(c:Controller):(ControllerState, OutputEvent) =   oldState.asInstanceOf[SwitchCardControllerState].switchCards(index, mode, c)
  override def undoStep(c:Controller):(ControllerState, OutputEvent) = (oldState, new TurnEndedEvent)

class DiscardCommand(indices: List[List[Int]], oldState: ControllerState) extends Command:
  override def doStep(c:Controller):(ControllerState, OutputEvent) =   oldState.asInstanceOf[DiscardControllerState].discardCards(indices, c)
  override def undoStep(c:Controller):(ControllerState, OutputEvent) = (oldState, new GoToDiscardEvent)

class NoDiscardCommand(oldState: ControllerState) extends Command:
  override def doStep(c:Controller):(ControllerState, OutputEvent) =   oldState.asInstanceOf[DiscardControllerState].skipDiscard(c)
  override def undoStep(c:Controller):(ControllerState, OutputEvent) = (oldState, new GoToDiscardEvent)

class InjectCommand(receiving_player:Int, cardIndex:Int, stashIndex:Int, position:Int, oldState: ControllerState) extends Command:
  override def doStep(c:Controller):(ControllerState, OutputEvent) =   oldState.asInstanceOf[InjectControllerState].injectCard(receiving_player, cardIndex, stashIndex, position, c)
  override def undoStep(c:Controller):(ControllerState, OutputEvent) = (oldState, new GoToInjectEvent)

class NoInjectCommand(oldState: ControllerState) extends Command:
  override def doStep(c:Controller):(ControllerState, OutputEvent) =   oldState.asInstanceOf[InjectControllerState].skipInject(c)
  override def undoStep(c:Controller):(ControllerState, OutputEvent) = (oldState, new GoToInjectEvent)