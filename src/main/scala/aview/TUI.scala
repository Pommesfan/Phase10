package aview

import model.Card
import utils.{CardSwitchedEvent, DoCreatePlayersEvent, DoDiscardEvent, DoSwitchCardEvent, GameStartedEvent, Observer, OutputEvent, TurnEndedEvent}
import controller.{Controller, GameRunningControllerState}

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
            case CREATE_PLAYERS => controller.solve(new DoCreatePlayersEvent(input.split(" ").toList))
            case SWITCH => {
              val inputs = input.split(" ").toList
              controller.solve(new DoSwitchCardEvent(inputs(0).toInt, inputs(1)))
            }
            case DISCARD =>
              controller.solve(new DoDiscardEvent(getCardsToDiscard(input)))

    }.start()

  def update(e: OutputEvent): String =
    val s = e match
      case e: GameStartedEvent =>
        mode = CREATE_PLAYERS
        "Namen eingeben:"
      case e: OutputEvent =>
        val g = controller.getGameData
        val r = g._1
        val t = g._2
        e match
          case e: CardSwitchedEvent =>
            mode = DISCARD
            "Abzulegende Karten angeben oder n für nicht ablegen:"
          case e: TurnEndedEvent =>
            def currentPlayer = t.current_player
            def playerName = controller.getPlayers()
            mode = SWITCH
            printPlayerStatus(playerName(currentPlayer), t.cardStash(t.current_player), t.openCard)
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
