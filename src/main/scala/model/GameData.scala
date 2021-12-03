package model
import controller.ValidatorStrategy
import model.Card

case class RoundData(validators:List[ValidatorStrategy])
case class TurnData(current_player:Int, cardStash:List[List[Card]], openCard:Card, discardedStash: List[Option[List[List[Card]]]])
