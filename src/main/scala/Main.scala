import Main.s
import controller.Controller

import java.util.Scanner
import scala.util.Random
import utils.Utils
import model.Card

object Main {
  val s = new Scanner(System.in)

  @main def hello: Unit =
    println("I am cardgame Phase10!")
    println(msg)

    val controller = new Controller

    val players = s.nextLine().split(" ")
    var current_player = 0
    var cardStash = controller.createCardStash(players.length)
    var openCard = controller.createCard
    var discardedStash = List.fill(players.size)(List[Card]())
    var player_has_discarded = Array.fill(players.size)(false)

    while (true)
      printPlayerStatus(players(current_player), cardStash(current_player), openCard)
      val card_index = s.nextLine().toInt
      val mode = s.nextLine()
      val result = controller.change_card(card_index, current_player, openCard, cardStash, mode)
      cardStash = result._1
      openCard = result._2

      if(!player_has_discarded(current_player))
        val discard_input = getCardsToDiscard()
        //apply changes if cards to discard selected
        if(discard_input.nonEmpty)
          val card_indices = discard_input.get
          val result_discard = controller.discard_cards(current_player, card_indices, cardStash, discardedStash)
          cardStash = result_discard._1
          discardedStash = result_discard._2
          player_has_discarded(current_player) = true

      current_player = controller.nextPlayer(current_player, players.length)
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

