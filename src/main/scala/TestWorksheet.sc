import scala.util.Random

val l = List(234,45,345635,122)

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

case class PlayingField(cards_to_Player:List[List[Card]], discardedCardsToPlayer:List[List[Card]], currentPlayer:Int)

val r = new Random()
def randomColor = r.nextInt(4)
def randomValue = r.nextInt(12)
def createCard = Card(randomColor + 1, randomValue + 1)
def createPlayerCardStash = List.fill(10)(createCard)
def createEmptyCardStash = List[Card]()
def NUMBER_OF_PLAYERS = 2

val c1 = Card(2,8)
val c2 = Card(3,11)
c1.toString
c2.toString

val field = new PlayingField(
  List.fill(NUMBER_OF_PLAYERS)(createPlayerCardStash),
  List.fill(NUMBER_OF_PLAYERS)(createEmptyCardStash),
  0)

case class PlayingField(players: List[String], activePlayer:Int, cards:List[List[Card]])

val field = PlayingField(
  List("Player A", "Player B"),
  0,
  List.fill(NUMBER_OF_PLAYERS)(createPlayerCardStash)
)

"Farbe: Grün; Wert = 13".matches("Farbe:\\s(Blau|Gelb|Grün|Rot);\\sWert\\s=\\s([1-9]|(1[0-2]))")
