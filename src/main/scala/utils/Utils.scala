package utils

import scala.util.Random
import model.{Card, JokerCard, RegularCard}

object Utils {
  val INJECT_TO_FRONT = 1
  val INJECT_AFTER = 2
  val NEW_CARD = 1
  val OPENCARD = 2

  val cardWidth = 120.0
  val cardProportion = 1.5
  val NumberSizeProportion = 1.0
  val space_between_cardstashes = 8

  private val r = new Random()
  def randomColor: Int = r.nextInt(4)
  def randomValue: Int = r.nextInt(12)
  def selectJoker: Boolean = r.nextInt(100) < 10;

  def inverseIndexList(indexList:List[Int], maxIndex:Int): List[Int] =
    List.range(0, 10).partition(n => !indexList.contains(n))._1

  def indexesUnique(l:List[Int]): Boolean = {
    val sorted = l.sortWith((a,b) => a < b)
    for(i <- 0 until sorted.size - 1) {
      if(sorted(i) == sorted(i + 1))
        return false
    }
    true
  }

  def resolveMultiples(cards : List[Card]): Boolean = {
    val firstRegular = getFirstRegularCard(cards)
    firstRegular match {
      case some: Some[RegularCard] =>
        val commonValue = some.get.value
        for (c <- cards) {
          c match
            case r: RegularCard =>
              if (r.value != commonValue) {
                return false
              }
        }
        true
      case _ => true
    }
  }

  def resolveSameColor(cards: List[Card]): Boolean = {
    val firstRegular = getFirstRegularCard(cards)
    firstRegular match {
      case some: Some[RegularCard] =>
        val commonColor = some.get.color
        for (c <- cards) {
          c match
            case r: RegularCard =>
              if (r.color != commonColor) {
                return false
              }
        }
        true
      case _ => true
    }
  }

  def resolveSequence(cards: List[Card]): Boolean = {
    var currentValue = 0;
    def increment(): Unit = if (currentValue == 12) currentValue = 1 else currentValue += 1
    val opt = getFirstRegularCard(cards)
    opt match
      case Some(s) =>
        val (start, c) = s
        for (idx <- start until cards.size) {
          increment()
          cards(idx) match {
            case r: RegularCard => if(r.value != currentValue) return false
          }
        }
        true
      case _ => true
  }

  def makeGroupedIndexList(indices:String, numberOfInputs:List[Int]):List[List[Int]] =
    indices.split(":").toList.map { s =>
      s.trim.split(" ").map(n => n.toInt).toList
    }
    
  def groupCardIndexes(indices:List[Int], numberOfInputs:List[Int]):List[List[Int]] =
    var start = 0
    var list = List[List[Int]]()
    for(i <- numberOfInputs)
      list = list :+ indices.slice(start, start + i)
      start = i
    list

  def fitToSequence(cards: List[Card], cardToInject: RegularCard, position: Int, idxFirstRegular: Int, firstRegular: RegularCard):Boolean =
    def keepInSequence(v: Int): Int = if(v > 12) v - 12 else v

    if(position == INJECT_TO_FRONT)
      keepInSequence(cardToInject.value + idxFirstRegular + 1) == firstRegular.value
    else if(position == INJECT_AFTER)
      keepInSequence(firstRegular.value + cards.length - idxFirstRegular) == cardToInject.value
    else
      throw new IllegalArgumentException

  def getFirstRegularCard(cards: List[Card]): Option[(Int, RegularCard)] =
    for (i <- 0 to cards.length)
      cards(i) match
        case c: RegularCard => return Some((i, c))
    None

  abstract class IndexListener:
    val index:Int
    def onListen(index:Int): Unit
}
