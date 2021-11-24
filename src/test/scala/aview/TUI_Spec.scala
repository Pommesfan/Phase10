package aview
import controller.{Controller, GameRunningControllerState}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import utils.{CardSwitchedEvent, DoCreatePlayersEvent, GameStartedEvent, TurnEndedEvent}

class UtilsSpec extends AnyWordSpec {
  "A TUI" when {
    val c = new Controller
    val initialState = c.solve(new DoCreatePlayersEvent(List("Player A", "Player B")))
    val tui = new TUI(c)
    "Asking for player name after programm started" in {
      tui.update(new GameStartedEvent) should be("Namen eingeben:")
    }
    "when card switched ask to discard" in {
      tui.update(new CardSwitchedEvent) should be("Abzulegende Karten angeben oder n für nicht ablegen:")
    }
    "showing status for current player when his turn starts" in {
      val regexCard = "Farbe:\\s(Blau|Gelb|Grün|Rot);\\sWert\\s=\\s([1-9]|(1[0-2]))"
      val s1 = tui.update(new TurnEndedEvent)
      val s = s1.split("\n")
      def initialState2 = initialState.asInstanceOf[GameRunningControllerState]
      s(0) should be("Aktueller Spieler: " + initialState2.players(initialState2.currentPlayer))
      s(1) should be("")
      s(2) should be("Offenliegende Karte:")
      s(3).matches(regexCard) should be(true)
      s(4) should be("")
      s(5)  should be("Karten des Spielers:")
      for (i <- 6 until 16)
        s(i).matches((i - 6).toString + ": " + regexCard) should be(true)
    }
    "method getCardToDiscard() returns None by parameter 'n' or makes Int-List from Number_List as String" in {
      tui.getCardsToDiscard("n") should be(None)
      tui.getCardsToDiscard("9 3 6 5 7 4") should be(Some(List(9, 3, 6, 5, 7, 4)))
    }
  }
}
