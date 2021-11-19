package aview
import controller.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import utils.{CardSwitchedEvent, GameStartedEvent, TurnEndedEvent}

class UtilsSpec extends AnyWordSpec {
  "A TUI" when {
    val c = new Controller
    c.doCreatePlayers(List("Player A", "Player B"))
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
      s(0) should be("Aktueller Spieler: " + c.getPlayers(c.getCurrentPlayer))
      s(1) should be("")
      s(2) should be("Offenliegende Karte:")
      s(3).matches(regexCard) should be(true)
      s(4) should be("")
      s(5)  should be("Karten des Spielers:")
      for (i <- 6 until 16)
        s(i).matches(regexCard) should be(true)
    }
  }
}
