package aview

import model.Card
import utils.{CardSwitchedEvent, Event, GameStartedEvent, TurnEndedEvent, Observer}
import controller.Controller

import java.util.Scanner

class TUI(controller: Controller) extends Observer {
  val CREATE_PLAYERS = 1
  val SWITCH = 2
  val DISCARD = 3

  private var mode = 0

  val sc = new Scanner(System.in)

  def start(): Unit =
    controller.add(this)
    new Thread {
      override def run(): Unit =
        while (true)

          val input = sc.nextLine()
          mode match
            case CREATE_PLAYERS => controller.doCreatePlayers(input.split(" ").toList)
            case SWITCH => {
              val inputs = input.split(" ").toList
              controller.doChangeCard(inputs(0).toInt, inputs(1))
            }
            case DISCARD =>
              controller.doDiscard(getCardsToDiscard(input))

    }.start()

  def update(e: Event): String =
    val s = e match
      case e: GameStartedEvent =>
        mode = CREATE_PLAYERS
        "Namen eingeben:"
      case e: CardSwitchedEvent =>
        mode = DISCARD
        "Abzulegende Karten angeben oder n für nicht ablegen:"
      case e: TurnEndedEvent =>
        def currentPlayer = controller.getCurrentPlayer
        def playerName = controller.getPlayers(currentPlayer)
        mode = SWITCH
        printPlayerStatus(playerName, controller.getCardStash(currentPlayer), controller.getOpenCard)
    println(s)
    s

  def printPlayerStatus(player: String, cards: List[Card], openCard: Card) : String =
    val sb = new StringBuilder
    sb.append("Aktueller Spieler: " + player)
    sb.append("\n\nOffenliegende Karte:\n")
    sb.append(openCard)
    sb.append("\n\nKarten des Spielers:\n")
    cards.zipWithIndex.foreach((c,i) => sb.append(i.toString + ": " + c.toString + '\n'))
    sb.append("\nAuszutauschende Karte angeben + Offenliegende oder neue nehmen (open/new)")
    sb.toString()

  def getCardsToDiscard(input: String):Option[List[Int]] =
    if(input == "n")
      None
    else
      Some(input.split(" ").map(n => n.toInt).toList)
}
