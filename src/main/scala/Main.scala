import java.util.Scanner
import scala.util.Random

@main def hello: Unit =
  println("I am cardgame Phase10!")
  println(msg)

  val s = new Scanner(System.in)

  val players = s.nextLine().split(" ")
  var current_player = 0
  var cardStash = createCardStash(players.length)
  var openCard = createCard

  while(true)
    println("Aktueller Spieler: " + players(current_player))
    println("Offenliegende Karte")
    println(openCard)
    println("Karten des Spielers:")
    for(c <- cardStash(current_player))
      println(c)

    println("Auszutauschende Karte angeben")
    val card_index = s.nextInt()
    val result = change_card(card_index, current_player, openCard, cardStash)
    cardStash = result._1
    openCard = result._2

    current_player = nextPlayer(current_player, players.length)

def msg = "I was compiled by Scala 3. :)"

private def nextPlayer(currentPlayer:Int, numberOfPlayers:Int):Int = (currentPlayer + 1) % numberOfPlayers

private def createCard: Card = Card(randomColor + 1, randomValue + 1)

private def createCardStash(numberOfPlayers:Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))

private def change_card(cardIndex:Int, playerIndex:Int, oldOpenCard : Card, oldCardStash: List[List[Card]]): (List[List[Card]], Card) = {
  def oldSubList = oldCardStash(playerIndex)
  def newSubList = oldSubList.updated(cardIndex, oldOpenCard)

  def newStash = oldCardStash.updated(playerIndex, newSubList)
  def leftCard = oldSubList(cardIndex)

  (newStash, leftCard)
}

val r = new Random()
def randomColor = r.nextInt(4)
def randomValue = r.nextInt(12)

case class Card(color:Int, value:Int) {
  override def toString: String = {
    def colorName: String = color match {
      case 1 => "Rot"
      case 2 => "Gelb"
      case 3 => "Blau"
      case 4 => "Grün"
    }
    "Farbe: " + colorName + "; Wert = " + value.toString
  }

  override def equals(obj: Any): Boolean = {
    if(!obj.isInstanceOf[Card])
      false
    else
      def c = obj.asInstanceOf[Card]
      c.value == value && c.color == color
  }
}