import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class Test1 extends AnyWordSpec:
    "A Phase 10 Card" when {
        msg should be("I was compiled by Scala 3. :)\nSpielernamen eingeben")
        "A Card " when {
            val card = Card(2, 5)
            "atrributes are correctly set" in {
                card.color should be(2)
                card.value should be(5)
            }
            "toString() is correctly implemented" in {
                card.toString should be("Farbe: Gelb; Wert = 5")
            }

            "equals() is works correct" when {
                val cards_to_compare = List(Card(3, 8), Card(3, 8), Card(4, 8), Card(3, 9), Card(1, 7))
                "same cards return true" in {
                    cards_to_compare(0).equals(cards_to_compare(1)) should be(true)
                }
                "card with on other attribute or at third both" in {
                    cards_to_compare(0).equals(cards_to_compare(2)) should be(false)
                    cards_to_compare(0).equals(cards_to_compare(3)) should be(false)
                    cards_to_compare(0).equals(cards_to_compare(4)) should be(false)
                }
            }
        }

        "methods for game controll are correct" when {
            "printing status of current player" when {
                val s = printPlayerStatus("Hallo", (List.fill(10)(createCard)), createCard)
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
            "nextplayer return following number but zero if it would be beyond number of players" in {
                nextPlayer(0, 4) should be(1)
                nextPlayer(1, 4) should be(2)
                nextPlayer(2, 4) should be(3)
                nextPlayer(3, 4) should be(0)
            }

            "create an card randomly" when {
                val c = createCard
                "colors ranges from 1 to 4 and values from 1 to 12" in {
                    c.color >= 1 && c.color <= 4 should be(true)
                    c.value >= 1 && c.value <= 12 should be(true)
                }
            }

            "create card stahses for players" when {
                val stash = createCardStash(2)
                "number of cardstashes for player is number of plyers" in {
                    stash.size should be(2)
                }
                "size of every stash is 10" in {
                    for (l <- stash)
                        l.size should be(10)
                }
            }

            "changing cards" when {
                //Test of changing cards
                def CHANGED_CARD = 8
                def PLAYER_INDEX = 1
                def NUMBER_OF_PLAYERS = 2

                val openCard = createCard
                val stash2 = createCardStash(NUMBER_OF_PLAYERS)
                val result = change_card(CHANGED_CARD, PLAYER_INDEX, openCard, stash2)

                def newStash = result._1
                def newOpenCard = result._2

                "selected card is replaced with open card" in {
                    newStash(PLAYER_INDEX)(CHANGED_CARD).equals(openCard) should be(true)
                }

                "new open card is one which was dropped by replacement in last statement" in {
                    newOpenCard.equals(stash2(PLAYER_INDEX)(CHANGED_CARD)) should be(true)
                }

                "other cards ecxept of selected from current player are not changed" in {
                    for (p <- 0 until NUMBER_OF_PLAYERS)
                        for (c <- 0 until stash2.size)
                            if (!(p == PLAYER_INDEX && c == CHANGED_CARD))
                                stash2(p)(c).equals(newStash(p)(c)) should be(true)
                }
            }
            "moves cards from stash to discarded-stash" when {
                val NUMBER_OF_PLAYERS = 2
                val stash = createCardStash(NUMBER_OF_PLAYERS)
                val discardedStash = List.fill(NUMBER_OF_PLAYERS)(List[Card]())
                val INDICES = List(0,1,2)
                val res = discard_cards(1, INDICES, stash, discardedStash)
                "moves cards of current player correctly" in {
                    val newStash = res._1
                    val newDiscardedStash = res._2
                    newStash(1).size should be(7)
                    newStash(0).size should be(10)
                    newDiscardedStash(1).size should be(3)
                    newDiscardedStash(0).size should be(0)
                }
            }
            "make inverse index list" when {
                val indices = List(4,5,6,7)
                "returns indexes from 1 to 10 except of the ones just defined" in {
                    inverseIndexList(indices, 10) should be(List(0,1,2,3,8,9))
                }
            }
        }
    }