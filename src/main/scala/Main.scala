import java.util.Scanner
import scala.util.Random

@main def hello: Unit =
  println("I am cardgame Phase10!")
  println(msg)

  val s = new Scanner(System.in)

  val players = s.nextLine().split(" ")
  var current_player = 0
  var cardStash = createCardStash(players.length)

  while(true)
    println("Aktueller Spieler: " + players(current_player))
    println("Auszutauschende Karte angeben")
    println("Karten des Spielers:")
    for(c <- cardStash(current_player))
      println(c)
    Thread.sleep(1000)

    current_player = nextPlayer(current_player, players.length)

def msg = "I was compiled by Scala 3. :)"

private def nextPlayer(currentPlayer:Int, numberOfPlayers:Int):Int = (currentPlayer + 1) % numberOfPlayers

private def createCard: Card = Card(randomColor + 1, randomValue + 1)

private def createCardStash(numberOfPlayers:Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))

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