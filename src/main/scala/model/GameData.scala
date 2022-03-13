package model

import controller.{ValidatorFactoryInterface, ValidatorStrategyInterface}
import model.Card
import model.{PlayerCardDeck,DiscardedCardDeck}

case class RoundData(validators:List[ValidatorStrategyInterface], errorPoints:List[Int])
case class TurnData(current_player:Int, playerCardDeck: PlayerCardDeck, openCard:Card, discardedCardDeck: DiscardedCardDeck)
