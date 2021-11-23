package controller

import model.Card
import utils.{CardSwitchedEvent, GameStartedEvent, TurnEndedEvent, Utils, Observable}
import Utils.{randomColor, randomValue}
import scala.util.Random

class Controller extends Observable {

  private var players = List[String]()
  private var current_player = 0
  private var cardStash = List[List[Card]]()
  private var openCard: Card = createCard
  private var discardedStash = List[List[Card]]()
  private var player_has_discarded = List[Boolean]()
  private var phases = List[Int]()
  private var validators = List[ValidatorStrategy]()

  def getPlayers = players
  def getCurrentPlayer = current_player
  def getCardStash = cardStash
  def getOpenCard = openCard
  def getDiscardedStash = discardedStash
  def getPlayerHasDiscarded = player_has_discarded

  def createCard: Card = Card(randomColor + 1, randomValue + 1)
  def createCardStash(numberOfPlayers: Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))
  def nextPlayer(currentPlayer: Int, numberOfPlayers: Int): Int = (currentPlayer + 1) % numberOfPlayers

  def change_card(cardIndex: Int, playerIndex: Int, oldOpenCard: Card, oldCardStash: List[List[Card]], mode: String): (List[List[Card]], Card) =
    def oldSubList = oldCardStash(playerIndex)
    def newSubList =
      if (mode == "open") oldSubList.updated(cardIndex, oldOpenCard)
      else if (mode == "new") oldSubList.updated(cardIndex, createCard)
      else throw new IllegalArgumentException

    def newStash = oldCardStash.updated(playerIndex, newSubList)
    def leftCard = oldSubList(cardIndex)

    (newStash, leftCard)

  def discard_cards(current_player: Int, card_indices: List[Int], cardStash: List[List[Card]], discardedStash: List[List[Card]]): (List[List[Card]], List[List[Card]]) =
    def playerCards = cardStash(current_player)
    def sublist_newCardstash = Utils.inverseIndexList(card_indices, cardStash(current_player).size).map(n => playerCards(n))
    def sublist_newDiscardedCards = card_indices.map(n => playerCards(n))
    def newCardStash = cardStash.updated(current_player, sublist_newCardstash)
    def newDiscardedStash = discardedStash.updated(current_player, sublist_newDiscardedCards)

    (newCardStash, newDiscardedStash)

  def doCreatePlayers(pPlayers: List[String]) =
    def numberOfPlayers = pPlayers.size
    players = pPlayers
    cardStash = createCardStash(numberOfPlayers)
    discardedStash = List.fill(numberOfPlayers)(List[Card]())
    player_has_discarded = List.fill(numberOfPlayers)(false)
    phases = List.fill(numberOfPlayers)(1)
    validators = List.fill(numberOfPlayers)(Validator.getValidator(1))
    notifyObservers(new TurnEndedEvent)

  def doChangeCard(index: Int, mode: String) =
    val res = change_card(index, current_player, openCard, cardStash, mode)
    cardStash = res._1
    openCard = res._2
    if(player_has_discarded(current_player))
      current_player = nextPlayer(current_player, players.size)
      notifyObservers(new TurnEndedEvent)
    else
      notifyObservers(new CardSwitchedEvent)

  def doDiscard(indices: Option[List[Int]]) =
    if (indices.nonEmpty && validators(current_player).validate(cardStash(current_player), indices.get))
      val list = indices.get
      val res = discard_cards(current_player, list, cardStash, discardedStash)
      cardStash = res._1
      discardedStash = res._2
      player_has_discarded = player_has_discarded.updated(current_player, true)
    current_player = nextPlayer(current_player, players.size)
    notifyObservers(new TurnEndedEvent)
}
