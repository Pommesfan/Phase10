package controller

import utils.Utils
import Utils.{MULTIPLES, SAME_COLOR, SEQUENCE}
import model.Card

object Validator {
  def getValidator(index : Int): ValidatorStrategy = index match {
    case 1 => new PhaseValidator(1) {
      override protected val cardGroups: List[CardGroup] = List(new CardGroup(MULTIPLES, 3), new CardGroup(MULTIPLES, 3))
      override def description(): String = "Zwei Drillinge"
    }
    case 2 => new PhaseValidator(2) {
      override protected val cardGroups: List[CardGroup] = List(new CardGroup(MULTIPLES, 3), new CardGroup(SEQUENCE, 4))
      override def description(): String = "Drilling und Viererfolge"
    }
  }
}

trait ValidatorStrategy:
  def description() : String
  def validate(cards:List[Card], numbers:List[Int]) : Boolean
  def getNumberOfInputs() : List[Int]
  def getNumberOfPhase() : Int

private class CardGroup(val groupType:Int, val numberOfCards:Int)

private abstract class PhaseValidator(val numberOfPhase:Int) extends ValidatorStrategy:
  protected val cardGroups: List[CardGroup]
  override def getNumberOfPhase(): Int = numberOfPhase
  override def getNumberOfInputs(): List[Int] = cardGroups.map(cg => cg.numberOfCards)
  override def validate(cards: List[Card], selectedCardIndexesFlat:List[Int]): Boolean =
    //no cards-index selected multiple
    if(!Utils.indexesUnique(selectedCardIndexesFlat)) return false

    val selectedCardIndexes = Utils.groupCardIndexes(selectedCardIndexesFlat, getNumberOfInputs())

    val card_stashes = selectedCardIndexes.map(l => l.map(n => cards(n)))
    val group_types = cardGroups.map(cg => cg.groupType)
    cardGroups.indices.foreach {idx =>
      def subList = card_stashes(idx)
      def number_of_cards = cardGroups(idx).numberOfCards

      def enoughCards = subList.size == number_of_cards
      def validateCardGroup: Boolean = group_types(idx) match
        case SEQUENCE => Utils.resolveSequence(subList)
        case MULTIPLES => Utils.resolveMultiples(subList)
        case SAME_COLOR => Utils.resolveSameColor(subList)

      if (!(enoughCards && validateCardGroup)) return false
    }
    true