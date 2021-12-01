package aview
import controller.{Controller, GameRunningControllerState}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import utils.{GoToDiscardEvent, DoCreatePlayersEvent, GameStartedEvent, TurnEndedEvent}

class TUI_Spec extends AnyWordSpec {
  "A TUI" when {
    val c = new Controller
    val initialState = c.solve(new DoCreatePlayersEvent(List("Player A", "Player B")))
    val tui = new TUI(c)
    "Asking for player name after programm started" in {
      tui.update(new GameStartedEvent) should be("Namen eingeben:")
    }
    "when card switched ask to discard" in {
      tui.update(new GoToDiscardEvent) should be("Abzulegende Karten angeben oder n für nicht ablegen:")
    }
    "showing status for current player when his turn starts" in {
      val regexCard = "Farbe:\\s(Blau|Gelb|Grün|Rot);\\sWert\\s=\\s([1-9]|(1[0-2]))"
      val s1 = tui.update(new TurnEndedEvent)
      val s = s1.split("\n")
      def initialState2 = initialState.asInstanceOf[GameRunningControllerState]
      s(0) should be("Abgelegte Karten")
      s(1) should be("Player A")
      s(2) should be("Keine Karten")
      s(3) should be("Player B")
      s(4) should be("Keine Karten")
      s(5) should be("")
      s(6) should be("Aktueller Spieler: " + initialState2.players(initialState2.currentPlayer))
      s(7) should be("")
      s(8) should be("Offenliegende Karte:")
      s(9).matches(regexCard) should be(true)
      s(10) should be("")
      s(11)  should be("Karten des Spielers:")
      for (i <- 12 until 22)
        s(i).matches((i - 12).toString + ": " + regexCard) should be(true)
    }
    "method getCardToDiscard() returns None by parameter 'n' or makes Int-List from Number_List as String" in {
      tui.getCardsToDiscard("n") should be(None)
      tui.getCardsToDiscard("9 3 6 ; 5 7 4") should be(Some(List(List(9, 3, 6), List(5, 7, 4))))
    }
  }
}
