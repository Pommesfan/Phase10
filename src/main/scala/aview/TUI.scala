package aview

import model.Card
import utils.{DoCreatePlayersEvent, DoDiscardEvent, DoInjectEvent, DoSwitchCardEvent, GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, Observer, OutputEvent, TurnEndedEvent, Utils}
import controller.{Controller, GameRunningControllerState}
import Utils.{INJECT_AFTER, INJECT_TO_FRONT, NEW_CARD, OPENCARD}

import java.util.Scanner

class TUI(controller: Controller) extends Observer {
  val CREATE_PLAYERS = 1
  val SWITCH = 2
  val DISCARD = 3
  val INJECT = 4

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
              def index = inputs(0)
              def mode = inputs(1) match {
                case "new" => NEW_CARD
                case "open" => OPENCARD
              }
              controller.solve(new DoSwitchCardEvent(inputs(0).toInt, mode))
            }
            case DISCARD =>
              controller.solve(new DoDiscardEvent(getCardsToDiscard(input)))
            case INJECT =>
              def inputs =
                if(input == "n")
                  None
                else
                  val l = input.split(" ")
                  val pos = if(l(3) == "FRONT") INJECT_TO_FRONT else if(l(3)=="AFTER") INJECT_AFTER else throw new IllegalArgumentException
                  Some(l(0).toInt, l(1).toInt, l(2).toInt, pos)
              controller.solve(new DoInjectEvent(inputs))

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
          case e: GoToInjectEvent =>
            mode = INJECT
            "Karten ablegen? Angabe: Spieler, Karte, Stapel, Position (FRONT/AFTER)"
          case e: GoToDiscardEvent =>
            mode = DISCARD
            "Abzulegende Karten angeben oder n für nicht ablegen:"
          case e: TurnEndedEvent =>
            def currentPlayer = t.current_player
            def playerName = controller.getPlayers()
            mode = SWITCH
            printDiscardedCards(playerName, t.discardedStash) + printPlayerStatus(playerName(currentPlayer), t.cardStash(t.current_player), t.openCard)
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

  def printDiscardedCards(playerNames:List[String], discardedCards:List[Option[List[List[Card]]]]): String =
    val sb = new StringBuilder
    sb.append("Abgelegte Karten\n")
    for(idx <- playerNames.indices)
      sb.append(playerNames(idx) + "\n")
      discardedCards(idx) match
        case s: Some[List[List[Card]]] =>
          s.get.foreach{ c =>
            c.zipWithIndex.foreach((c,i) => sb.append(i.toString + ": " + c.toString + '\n'))
          }
        case None => sb.append("Keine Karten\n")
    sb.append("\n")
    sb.toString()

  def getCardsToDiscard(input: String):Option[List[List[Int]]] =
    if(input == "n")
      None
    else
      val g = controller.getGameData
      def r = g._1
      def t = g._2
      def numberOfInputs = r.validators(t.current_player).getNumberOfInputs()
      Some(Utils.makeGroupedIndexList(input, numberOfInputs))
}
