package controller

import utils.Utils
import model.Card

object GroupType extends Enumeration:
  val MULTIPLES, SAME_COLOR, SEQUENCE = Value

object Validator {
  def getValidator(index : Int): ValidatorStrategy = index match {
    case 1 => new Phase1Validator
    case 2 => new Phase2Validator
  }
}

private class CardGroup(val groupType:GroupType.Value, val numberOfCards:Int)

abstract class ValidatorStrategy(val numberOfPhase:Int):
  protected val cardGroups: List[CardGroup]
  def description:String
  def getNumberOfPhase(): Int = numberOfPhase
  def getNumberOfInputs(): List[Int] = cardGroups.map(cg => cg.numberOfCards)
  def validate(cards: List[Card], selectedCardIndexes:List[List[Int]]): Boolean =
    //no cards-index selected multiple
    if(!Utils.indexesUnique(selectedCardIndexes.flatten)) return false

    val card_stashes = selectedCardIndexes.map(l => l.map(n => cards(n)))
    val group_types = cardGroups.map(cg => cg.groupType)
    cardGroups.indices.foreach {idx =>
      def subList = card_stashes(idx)
      def number_of_cards = cardGroups(idx).numberOfCards

      def enoughCards = subList.size == number_of_cards
      def validateCardGroup: Boolean = group_types(idx) match
        case GroupType.SEQUENCE => Utils.resolveSequence(subList)
        case GroupType.MULTIPLES => Utils.resolveMultiples(subList)
        case GroupType.SAME_COLOR => Utils.resolveSameColor(subList)

      if (!(enoughCards && validateCardGroup)) return false
    }
    true

private class Phase1Validator extends ValidatorStrategy(1):
  override protected val cardGroups: List[CardGroup] = List(new CardGroup(GroupType.MULTIPLES, 3), new CardGroup(GroupType.MULTIPLES, 3))
  override def description: String = "Zwei Drillinge"


private class Phase2Validator extends ValidatorStrategy(2):
  override protected val cardGroups: List[CardGroup] = List(new CardGroup(GroupType.MULTIPLES, 3), new CardGroup(GroupType.SEQUENCE, 4))
  override def description: String = "Drilling und Viererfolge"
