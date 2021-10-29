import java.util.Scanner
import scala.util.Random

object Main {
  @main def hello: Unit =
    println("I am cardgame Phase10!")
    println(msg)

    val s = new Scanner(System.in)

    val players = s.nextLine().split(" ")
    var current_player = 0
    var cardStash = createCardStash(players.length)
    var openCard = createCard

    while (true)
      printPlayerStatus(players(current_player), cardStash(current_player), openCard)
      val card_index = s.nextInt()
      val result = change_card(card_index, current_player, openCard, cardStash)
      cardStash = result._1
      openCard = result._2

      current_player = nextPlayer(current_player, players.length)
}

def printPlayerStatus(player: String, cards: List[Card], openCard: Card) : String =
  val sb = new StringBuilder
  sb.append("Aktueller Spieler: " + player)
  sb.append("\n\nOffenliegende Karte: \n")
  sb.append(openCard)
  sb.append("\n\nKarten des Spielers:\n")
  cards.foreach(c => sb.append(c.toString + '\n'))
  sb.append("\nAuszutauschende Karte angeben")
  println(sb)
  sb.toString()


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
}