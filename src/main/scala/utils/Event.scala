package utils

import model.Card

trait Event

trait OutputEvent extends Event

class ProgramStartedEvent extends OutputEvent
class GoToDiscardEvent extends OutputEvent
class GoToInjectEvent extends OutputEvent
class TurnEndedEvent(val newCard: Card) extends OutputEvent
class NewRoundEvent(val newCard: Card) extends OutputEvent
class GameStartedEvent(val newCard: Card) extends OutputEvent
