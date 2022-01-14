package aview
import controller.GameRunningControllerStateInterface
import controller.ControllerBaseImplement.{Controller, CreatePlayerCommand, DiscardCommand, InitialState, InjectCommand, NoDiscardCommand, NoInjectCommand, SwitchCardCommand}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import utils.{DoCreatePlayerEvent, DoDiscardEvent, DoInjectEvent, DoNoDiscardEvent, DoNoInjectEvent, DoSwitchCardEvent, GoToDiscardEvent, ProgramStartedEvent, TurnEndedEvent}
import model.Card

class TUI_Spec extends AnyWordSpec {
  "A TUI" when {
    val controller = new Controller
    val initialState = controller.solve(new DoCreatePlayerEvent(List("Player A", "Player B")), false)
    val tui = new TUI(controller)
    val regexCard = "Farbe:\\s(Blau|Gelb|Grün|Rot);\\sWert\\s=\\s([1-9]|(1[0-2]))"
    "Asking for player name after programm started" in {
      tui.update(new ProgramStartedEvent) should be("Namen eingeben:")
    }
    "when card switched ask to discard" in {
      val s = tui.update(new GoToDiscardEvent).split("\n")
      s(0) should be("Karten des Spielers:")
      for (i <- 1 until 11)
        s(i).matches((i - 1).toString + ": " + regexCard) should be(true)
      s(11) should be("")
      s(12) should be("Abzulegende Karten angeben oder n für nicht ablegen:")
    }
    "showing status for current player when his turn starts" in {
      val s1 = tui.update(new TurnEndedEvent(new Card(3,5)))
      val s = s1.split("\n")
      def initialState2 = initialState.asInstanceOf[GameRunningControllerStateInterface]
      s(0) should be("-" * 32)
      s(1) should be("Abgelegte Karten")
      s(2) should be("Player A")
      s(3) should be("Keine Karten")
      s(4) should be("Player B")
      s(5) should be("Keine Karten")
      s(6) should be("")
      s(7) should be("Aktueller Spieler: " + initialState2.players(initialState2.currentPlayer))
      s(8) should be("")
      s(9) should be("Neue Karte:")
      s(10).matches(regexCard) should be(true)
      s(11) should be("Offenliegende Karte:")
      s(12).matches(regexCard) should be(true)
      s(13) should be("")
      s(14)  should be("Karten des Spielers:")
      for (i <- 15 until 25)
        s(i).matches((i - 15).toString + ": " + regexCard) should be(true)
    }
    "method getCardToDiscard() returns None by parameter 'n' or makes Int-List from Number_List as String" in {
      tui.getCardsToDiscard("9 3 6 : 5 7 4") should be(List(List(9, 3, 6), List(5, 7, 4)))
    }
    "method createCommand() makes command from inputs accordingly game situation" when {
      "Switching cards" in {
        val c = tui.createInputEvent("1 new", tui.SWITCH)
        c.isInstanceOf[DoSwitchCardEvent] should be(true)
      }
      "Discard Cards" in {
        val c = tui.createInputEvent("0 1 2 : 3 4 5", tui.DISCARD)
        c.isInstanceOf[DoDiscardEvent] should be(true)
      }
      "Discard none" in {
        val c = tui.createInputEvent("n", tui.DISCARD)
        c.isInstanceOf[DoNoDiscardEvent] should be(true)
      }
      "Inject Card" in {
        val c = tui.createInputEvent("0 0 0 FRONT", tui.INJECT)
        c.isInstanceOf[DoInjectEvent] should be(true)
      }
      "Inject None" in {
        val c = tui.createInputEvent("n", tui.INJECT)
        c.isInstanceOf[DoNoInjectEvent] should be(true)
      }
    }
  }
}
