package utils

trait Event

class GameStartedEvent extends Event
class CardSwitchedEvent extends Event
class TurnEndedEvent extends Event