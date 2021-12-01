package utils

trait Event

trait OutputEvent extends Event

class GameStartedEvent extends OutputEvent
class GoToDiscardEvent extends OutputEvent
class GoToInjectEvent extends OutputEvent
class TurnEndedEvent extends OutputEvent

trait InputEvent extends Event

case class DoCreatePlayersEvent(players:List[String]) extends InputEvent
case class DoSwitchCardEvent(index:Int, mode:Int) extends InputEvent
case class DoDiscardEvent(indices:Option[List[List[Int]]]) extends InputEvent
case class DoInjectEvent(inputs:Option[(Int, Int, Int, Int)]) extends InputEvent