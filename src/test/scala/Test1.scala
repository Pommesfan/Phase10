import controller.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import model.Card

class Test1 extends AnyWordSpec:
    "A Phase 10 Cardgame" when {
        msg should be("I was compiled by Scala 3. :)\nSpielernamen eingeben")

        "methods for game controll are correct" when {
            "printing status of current player" when {
                val c = new Controller
                val s = printPlayerStatus("Hallo", (List.fill(10)(c.createCard)), c.createCard)
                val stringlist = s.split("\n").toList
                "prints current player" in {
                    stringlist(0) should be("Aktueller Spieler: Hallo")
                    stringlist(1) should be("")
                }
                "prints open card" in {
                    stringlist(2) should be("Offenliegende Karte: ")
                    stringlist(3).matches("Farbe:\\s(Blau|Gelb|Grün|Rot);\\sWert\\s=\\s([1-9]|(1[0-2]))") should be(true)
                    stringlist(4) should be("")
                }
                "prints playercardstash" in {
                    stringlist(5) should be("Karten des Spielers:")
                    for(i <- 6 until 16)
                        stringlist(i).matches("Farbe:\\s(Blau|Gelb|Grün|Rot);\\sWert\\s=\\s([1-9]|(1[0-2]))") should be(true)
                }
            }
        }
    }