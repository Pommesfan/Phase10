package controller

import model.Card
import utils.Utils

import java.util.Observable
import scala.util.Random

class Controller extends Observable{
  def nextPlayer(currentPlayer:Int, numberOfPlayers:Int):Int = (currentPlayer + 1) % numberOfPlayers

  def createCard: Card = Card(randomColor + 1, randomValue + 1)

  def createCardStash(numberOfPlayers:Int): List[List[Card]] = List.fill(numberOfPlayers)(List.fill(10)(createCard))

  def change_card(cardIndex:Int, playerIndex:Int, oldOpenCard : Card, oldCardStash: List[List[Card]], mode: String): (List[List[Card]], Card) =
    def oldSubList = oldCardStash(playerIndex)
    def newSubList =
      if(mode == "open") oldSubList.updated(cardIndex, oldOpenCard)
      else if(mode == "new") oldSubList.updated(cardIndex, createCard)
      else throw new IllegalArgumentException

    def newStash = oldCardStash.updated(playerIndex, newSubList)
    def leftCard = oldSubList(cardIndex)

    (newStash, leftCard)

  def discard_cards(current_player: Int, card_indices: List[Int], cardStash:List[List[Card]], discardedStash:List[List[Card]]): (List[List[Card]], List[List[Card]]) =
    def playerCards = cardStash(current_player)
    def sublist_newCardstash = Utils.inverseIndexList(card_indices, cardStash(current_player).size).map(n => playerCards(n))
    def sublist_newDiscardedCards = card_indices.map(n => playerCards(n))
    def newCardStash = cardStash.updated(current_player, sublist_newCardstash)
    def newDiscardedStash = discardedStash.updated(current_player, sublist_newDiscardedCards)
    (newCardStash, newDiscardedStash)

  val r = new Random()
  def randomColor = r.nextInt(4)
  def randomValue = r.nextInt(12)
}
