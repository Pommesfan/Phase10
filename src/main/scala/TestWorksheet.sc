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

val a = List(345,6756,345,76,3245,768,34,75)
val b = List(1,3,4)

def inverseIndexList(indexList:List[Int], maxIndex:Int): List[Int] =
  var new_index_list = List[Int]()
  for(i <- 0 until maxIndex)
    if(!indexList.contains(i))
      new_index_list = i::new_index_list
  new_index_list.reverse


inverseIndexList(b, 10)

def createCardStash(numberOfPlayers:Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))

def discard_cards(current_player: Int, card_indices: List[Int], cardStash:List[List[Card]], discardedStash:List[List[Card]]): (List[List[Card]], List[List[Card]]) =
  def playerCards = cardStash(current_player)
  def sublist_newDiscardedCards = card_indices.map(n => playerCards(n))
  def sublist_newCardstash = inverseIndexList(card_indices, cardStash(0).size).map(n => playerCards(n))
  def newCardStash = cardStash.updated(current_player, sublist_newCardstash)
  def newDiscardedStash = discardedStash.updated(current_player, sublist_newDiscardedCards)
  (newCardStash, newDiscardedStash)

val stash = createCardStash(2)

val res = discard_cards(0, List(0,1,2),
  stash,
  List.fill(2)(List[Card]())
)

stash.foreach(n => println(n.size))
res._1.foreach(n => println(n.size))
res._2.foreach(n => println(n.size))