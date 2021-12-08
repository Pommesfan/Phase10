package utils

import model.Card

trait Event

trait OutputEvent extends Event

class GameStartedEvent extends OutputEvent
class GoToDiscardEvent extends OutputEvent
class GoToInjectEvent extends OutputEvent
class TurnEndedEvent(val newCard: Card) extends OutputEvent
