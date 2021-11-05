import Main.s

import java.util.Scanner
import scala.util.Random

object Main {
  val s = new Scanner(System.in)

  @main def hello: Unit =
    println("I am cardgame Phase10!")
    println(msg)

    val players = s.nextLine().split(" ")
    var current_player = 0
    var cardStash = createCardStash(players.length)
    var openCard = createCard
    var discardedStash = List.fill(players.size)(List[Card]())
    var player_has_discarded = Array.fill(players.size)(false)

    while (true)
      printPlayerStatus(players(current_player), cardStash(current_player), openCard)
      val card_index = s.nextLine().toInt
      val mode = s.nextLine()
      val result = change_card(card_index, current_player, openCard, cardStash, mode)
      cardStash = result._1
      openCard = result._2

      if(!player_has_discarded(current_player))
        val discard_input = getCardsToDiscard()
        //apply changes if cards to discard selected
        if(discard_input.nonEmpty)
          val card_indices = discard_input.get
          val result_discard = discard_cards(current_player, card_indices, cardStash, discardedStash)
          cardStash = result_discard._1
          discardedStash = result_discard._2
          player_has_discarded(current_player) = true

      current_player = nextPlayer(current_player, players.length)
}

def printPlayerStatus(player: String, cards: List[Card], openCard: Card) : String =
  val sb = new StringBuilder
  sb.append("Aktueller Spieler: " + player)
  sb.append("\n\nOffenliegende Karte: \n")
  sb.append(openCard)
  sb.append("\n\nKarten des Spielers:\n")
  cards.foreach(c => sb.append(c.toString + '\n'))
  sb.append("\nAuszutauschende Karte angeben + Offenliegende oder neue nehmen (open/new)")
  println(sb)
  sb.toString()


def getCardsToDiscard():Option[List[Int]] =
  println("Abzulegende Karten angeben oder n für nicht ablegen")
  val input = s.nextLine()
  if(input == "n")
    None
  else
    Some(input.split(" ").map(n => n.toInt).toList)

def msg = "I was compiled by Scala 3. :)\nSpielernamen eingeben"

private def nextPlayer(currentPlayer:Int, numberOfPlayers:Int):Int = (currentPlayer + 1) % numberOfPlayers

private def createCard: Card = Card(randomColor + 1, randomValue + 1)

private def createCardStash(numberOfPlayers:Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))

private def change_card(cardIndex:Int, playerIndex:Int, oldOpenCard : Card, oldCardStash: List[List[Card]], mode: String): (List[List[Card]], Card) =
  def oldSubList = oldCardStash(playerIndex)
  def newSubList =
    if(mode == "open") oldSubList.updated(cardIndex, oldOpenCard)
    else if(mode == "new") oldSubList.updated(cardIndex, createCard)
    else throw new IllegalArgumentException

  def newStash = oldCardStash.updated(playerIndex, newSubList)
  def leftCard = oldSubList(cardIndex)

  (newStash, leftCard)

private def discard_cards(current_player: Int, card_indices: List[Int], cardStash:List[List[Card]], discardedStash:List[List[Card]]): (List[List[Card]], List[List[Card]]) =
  def playerCards = cardStash(current_player)
  def sublist_newCardstash = inverseIndexList(card_indices, cardStash(current_player).size).map(n => playerCards(n))
  def sublist_newDiscardedCards = card_indices.map(n => playerCards(n))
  def newCardStash = cardStash.updated(current_player, sublist_newCardstash)
  def newDiscardedStash = discardedStash.updated(current_player, sublist_newDiscardedCards)
  (newCardStash, newDiscardedStash)

private def inverseIndexList(indexList:List[Int], maxIndex:Int): List[Int] =
  var new_index_list = List[Int]()
  (0 until maxIndex).foreach(i =>
    if(!indexList.contains(i))
      new_index_list = i::new_index_list
  )
  new_index_list.reverse

val r = new Random()
def randomColor = r.nextInt(4)
def randomValue = r.nextInt(12)

case class Card(color:Int, value:Int) {
  override def toString: String = {
    def colorName: String = color match {
      case 1 => "Rot"
      case 2 => "Gelb"
      case 3 => "Blau"
      case 4 => "Grün"
    }
    "Farbe: " + colorName + "; Wert = " + value.toString
  }
}