package utils

import scala.util.Random
import model.Card

object Utils {
  val NEW_CARD = 1
  val OPENCARD = 2
  
  private val r = new Random()
  def randomColor = r.nextInt(4)
  def randomValue = r.nextInt(12)

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
    val commonValue = cards.head.value
    for (c <- cards) {
      if(c.value != commonValue){
        return false
      }
    }
    true
  }

  def resolveSequence(cards: List[Card]): Boolean = {
    var i = cards.head.value
    def increment(): Unit = if (i == 12) i = 1 else i += 1
    for (index <- 1 until cards.size) {
      increment()
      if (cards(index).value != i) return false
    }
    true
  }

  def resolveSameColor(cards: List[Card]): Boolean = {
    val commonColor = cards.head.color
    for(c <- cards) {
      if (c.color != commonColor) return false
    }
    true
  }

  def makeGroupedIndexList(indices:String, numberOfInputs:List[Int]):List[List[Int]] =
    indices.split(";").toList.map { s =>
      s.trim.split(" ").map(n => n.toInt).toList
    }
}
