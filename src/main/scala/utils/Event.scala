package utils

trait Event

trait OutputEvent extends Event

class GameStartedEvent extends OutputEvent
class CardSwitchedEvent extends OutputEvent
class TurnEndedEvent extends OutputEvent

trait InputEvent extends Event

case class DoCreatePlayersEvent(players:List[String]) extends InputEvent
case class DoSwitchCardEvent(index:Int, mode:String) extends InputEvent
case class DoDiscardEvent(indices:Option[List[List[Int]]]) extends InputEvent