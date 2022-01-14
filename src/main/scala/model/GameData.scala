package model
import controller.{ValidatorFactoryInterface, ValidatorStrategyInterface}
import model.Card

case class RoundData(validators:List[ValidatorStrategyInterface], errorPoints:List[Int])
case class TurnData(current_player:Int, cardStash:List[List[Card]], openCard:Card, discardedStash: List[Option[List[List[Card]]]])
