import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class Test1 extends AnyWordSpec:
    val card = Card(2, 5)
    card.color should be(2)
    card.value should be(5)
    card.toString should be("Farbe: Gelb; Wert = 5")

    val cards_to_compare = List(Card(3,8), Card(3,8), Card(4,8), Card(3,9), Card(1,7))

    cards_to_compare(0).equals(cards_to_compare(1)) should be(true)
    cards_to_compare(0).equals(cards_to_compare(2)) should be(false)
    cards_to_compare(0).equals(cards_to_compare(3)) should be(false)
    cards_to_compare(0).equals(cards_to_compare(4)) should be(false)

    msg should be("I was compiled by Scala 3. :)")
    nextPlayer(0, 4) should be(1)
    nextPlayer(1, 4) should be(2)
    nextPlayer(2, 4) should be(3)
    nextPlayer(3, 4) should be(0)

    val c = createCard
    c.color >= 1 && c.color <= 4 should be(true)
    c.value >= 1 && c.value <= 12 should be(true)

    val stash = createCardStash(2)
    stash.size should be(2)
    for (l <- stash)
        l.size should be(10)

    //Test of changing cards
    def CHANGED_CARD = 8
    def PLAYER_INDEX = 1
    def NUMBER_OF_PLAYERS = 2
    val openCard = createCard
    val stash2 = createCardStash(NUMBER_OF_PLAYERS)

    val result = change_card(CHANGED_CARD,PLAYER_INDEX, openCard, stash2)
    def newStash = result._1
    def newOpenCard = result._2

    //cards correctly changed
    newStash(PLAYER_INDEX)(CHANGED_CARD).equals(openCard) should be(true)
    newOpenCard.equals(stash2(PLAYER_INDEX)(CHANGED_CARD)) should be(true)

    //others are the same
    for(p <- 0 until NUMBER_OF_PLAYERS)
        for(c <- 0 until stash2.size)
            if(!(p == PLAYER_INDEX && c == CHANGED_CARD))
                stash2(p)(c).equals(newStash(p)(c)) should be(true)
