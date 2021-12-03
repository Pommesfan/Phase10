package aview

import model.Card
import utils.{GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, Observer, OutputEvent, TurnEndedEvent, Utils}
import controller.{Command, Controller, ControllerState, CreatePlayerCommand, DiscardCommand, GameRunningControllerState, InjectCommand, NoDiscardCommand, NoInjectCommand, SwitchCardCommand}
import Utils.{INJECT_AFTER, INJECT_TO_FRONT, NEW_CARD, OPENCARD}

import java.util.Scanner
import scala.util.{Failure, Success, Try}

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
          if(input == "undo")
            controller.undo
          else
            val command_try = Try(createCommand(input, controller.state))
            command_try match {
              case Success(command) => controller.solve(command)
              case Failure(command) => println("Eingaben ungültig")
            }
    }.start()

  def createCommand(input:String, state:ControllerState):Command = mode match
    case CREATE_PLAYERS => new CreatePlayerCommand(input.split(" ").toList, state)
    case SWITCH => {
      val inputs = input.split(" ").toList
      def index = inputs(0)
      def mode = inputs(1) match {
        case "new" => NEW_CARD
        case "open" => OPENCARD
      }
      new SwitchCardCommand(inputs(0).toInt, mode, state)
    }
    case DISCARD =>
      if(input == "n")
        new NoDiscardCommand(state)
      else
        new DiscardCommand(getCardsToDiscard(input), state)
    case INJECT =>
      if(input == "n")
        new NoInjectCommand(state)
      else
        val l = input.split(" ")
        val pos = if(l(3) == "FRONT") INJECT_TO_FRONT else if(l(3)=="AFTER") INJECT_AFTER else throw new IllegalArgumentException
        new InjectCommand(l(0).toInt, l(1).toInt, l(2).toInt, pos, state)

  def update(e: OutputEvent): String =
    val s = e match
      case e: GameStartedEvent =>
        mode = CREATE_PLAYERS
        "Namen eingeben:"
      case e: OutputEvent =>
        val g = controller.getGameData
        val r = g._1
        val t = g._2
        def currentPlayer = t.current_player
        val playerName = controller.getPlayers()
        e match
          case e: GoToInjectEvent =>
            mode = INJECT
            printDiscardedCards(playerName, t.discardedStash) + printPlayerStatus(playerName(currentPlayer), t.cardStash(currentPlayer), t.openCard) +
              "\nKarten anlegen? Angabe: Spieler, Karte, Stapel, Position (FRONT/AFTER)"
          case e: GoToDiscardEvent =>
            mode = DISCARD
            printPlayerStatus(playerName(currentPlayer), t.cardStash(currentPlayer), t.openCard) +
              "\nAbzulegende Karten angeben oder n für nicht ablegen:"
          case e: TurnEndedEvent =>
            mode = SWITCH
            printDiscardedCards(playerName, t.discardedStash) + printPlayerStatus(playerName(currentPlayer), t.cardStash(t.current_player), t.openCard) +
              "\nAuszutauschende Karte angeben + Offenliegende oder neue nehmen (open/new)"
    println(s)
    s

  def printPlayerStatus(player: String, cards: List[Card], openCard: Card) : String =
    val sb = new StringBuilder
    sb.append("Aktueller Spieler: " + player)
    sb.append("\n\nOffenliegende Karte:\n")
    sb.append(openCard)
    sb.append("\n\nKarten des Spielers:\n")
    cards.zipWithIndex.foreach((c,i) => sb.append(i.toString + ": " + c.toString + '\n'))
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

  def getCardsToDiscard(input: String):List[List[Int]] =
    val g = controller.getGameData
    def r = g._1
    def t = g._2
    def numberOfInputs = r.validators(t.current_player).getNumberOfInputs()
    Utils.makeGroupedIndexList(input, numberOfInputs)
}
