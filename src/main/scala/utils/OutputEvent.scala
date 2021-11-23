package utils

trait Event

trait OutputEvent extends Event

class GameStartedEvent extends OutputEvent
class CardSwitchedEvent extends OutputEvent
class TurnEndedEvent extends OutputEvent