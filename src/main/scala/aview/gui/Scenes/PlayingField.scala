package aview.gui.Scenes

import scalafx.scene.{Scene, layout}
import scalafx.scene.paint.Color
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.scene.control.{Alert, Button}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.shape.Rectangle
import controller.ControllerInterface
import model.{Card, DiscardedCardDeck, PlayerCardDeck, RoundData, TurnData}
import utils.{DoDiscardEvent, DoInjectEvent, DoNoDiscardEvent, DoNoInjectEvent, DoSwitchCardEvent, GameEndedEvent, GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, NewRoundEvent, OutputEvent, TurnEndedEvent, Utils}
import Utils.{INJECT_AFTER, INJECT_TO_FRONT, IndexListener, cardProportion, cardWidth}
import aview.gui.CardView

import scala.collection.mutable.ListBuffer

class PlayingField(controller: ControllerInterface, newCardInitial:Card) extends Scene {
  val SWITCH = 2
  val DISCARD = 3
  val INJECT = 4

  private var mode = SWITCH
  private var selectedPlayerCard = -1
  private var selectedPlayerToInject = -1
  private var selected_stash_to_inject = -1
  private var selected_position_to_inject = -1
  private val players = controller.getPlayers()
  private val listToSelect = new ListBuffer[Int]()

  var selectNewOrOpenCard = -1

  def getPlayerCardView(t:TurnData) = t.playerCardDeck.cards(t.current_player).indices.map(idx =>
    new CardView(t.playerCardDeck.cards(t.current_player)(idx), Some(new IndexListener {
      override val index: Int = idx
      override def onListen(index: Int): Unit = if (mode == SWITCH || mode == INJECT) selectedPlayerCard = index else listToSelect.append(index)
  }))).toSeq

  fill = Color.AliceBlue

  private def createField(r:RoundData, t:TurnData, newCard:Option[Card]) = new VBox {
    children = Seq(
      new VBox {
        children = Seq(
          // New and Open Card
          new HBox {
            children = Seq(
              new VBox {
                children = Seq(
                  new Text("Neue Karte"),
                  if(newCard.nonEmpty) {
                    new CardView(newCard.get, None) {
                      onMouseClicked = e => selectNewOrOpenCard = Utils.NEW_CARD
                    }
                  } else {
                    new Rectangle {
                      width = cardWidth
                      height = cardWidth * cardProportion
                      arcWidth = 40
                      fill = Color.Transparent
                    }
                  }
                )
              },
              new VBox {
                children = Seq(
                  new Text("Offenliegende Karte"),
                  new CardView(t.openCard, None) {
                    onMouseClicked = e => selectNewOrOpenCard = Utils.OPENCARD
                  }
                )
              },
              new VBox {
                //Buttons
                children = Seq(
                  new Text("Aktueller Spieler: " + players(t.current_player) + "; Phase " + r.validators(t.current_player).getNumberOfPhase() + ": " + r.validators(t.current_player).description),
                  new Button("Tauschen") {
                    disable = !(mode == SWITCH)
                    onMouseClicked = e => if(selectedPlayerCard != -1 && selectNewOrOpenCard != -1)controller.solve(new DoSwitchCardEvent(selectedPlayerCard, selectNewOrOpenCard))
                  },
                  new Button("Ablegen") {
                    disable = !(mode == DISCARD)
                    onMouseClicked = e =>
                      def g = controller.getGameData
                      def r = g._1
                      def t = g._2
                      val groupedCardIndexes = Utils.groupCardIndexes(listToSelect.toList, r.validators(t.current_player).getNumberOfInputs())
                      controller.solve(new DoDiscardEvent(groupedCardIndexes))
                  },
                  new Button("Anlegen") {
                    disable = !(mode == INJECT)
                    onMouseClicked = e =>
                      if(!(selectedPlayerToInject == -1 || selectedPlayerCard == -1 || selected_stash_to_inject == -1 || selected_position_to_inject == -1))
                        controller.solve(new DoInjectEvent(selectedPlayerToInject, selectedPlayerCard, selected_stash_to_inject, selected_position_to_inject))
                  },
                  new Button("nächster Spieler") {
                    disable = !(mode == DISCARD || mode == INJECT)
                    onMouseClicked = e => mode match {
                      case DISCARD => controller.solve(new DoNoDiscardEvent)
                      case INJECT => controller.solve(new DoNoInjectEvent)
                    }
                  },
                  new Button("Rückgängig") {
                    onMouseClicked = e => controller.undo
                  }
                )
              }
            )
          }
        )
      },
      new Text("Spielerkarten:"),
      new HBox {
        children = getPlayerCardView(t)
      },
      new Text("Abgelegte Karten:"),
      showDiscardedCards(t.discardedCardDeck)
    ),
  }

  def showDiscardedCards(discardedCardDeck: DiscardedCardDeck): VBox = {
    val vbox = new VBox()
    for(p <- players.indices)
      vbox.getChildren.add(new Text(players(p)))

      def showDiscardedStash():HBox =
        val hbox = new HBox()
        val concreteStash = discardedCardDeck.cards(p).get
        for(s <- concreteStash.indices)
          def cardGroup = concreteStash(s)
          hbox.getChildren.add(new SpaceRectangle(p, s, INJECT_TO_FRONT))
          for(card <- cardGroup)
            hbox.getChildren.add(new CardView(card, None))
          hbox.getChildren.add(new SpaceRectangle(p, s, INJECT_AFTER))
        hbox

      if(discardedCardDeck.cards(p).nonEmpty)
        vbox.getChildren.add(showDiscardedStash())
    vbox
  }

  def show_new_Round_Dialog(players:List[String], r: RoundData, t:TurnData): Unit = new Alert(AlertType.Information) {
    val build = new StringBuilder
    resizable = true
    def createString():Unit = players.zipWithIndex.map { p =>
      val name = p._1
      val index = p._2
      build.append("Spieler " + (index + 1) + " " + name + ": " + r.validators(index).description + "; Fehlerpunkte: " + r.errorPoints(index) + "\n")
    }
    title = "Neue Runde"
    headerText = "Folgende Phasen sowie Fehlerpunkte:"
    createString()
    contentText = build.toString()
  }.showAndWait()

  def moveUnvalidDialog(): Unit = new Alert(AlertType.Information) {
    resizable = true
    headerText = "Ungültiger Spielzug"
  }.showAndWait()

  def show_game_ended_Dialog(e: GameEndedEvent): Unit = new Alert(AlertType.Information) {
    def buildString(): String = {
      val b = new StringBuilder()
      for (p <- e.players.zipWithIndex) {
        def name  = p._1
        def idx = p._2
        b.append(name + ": Phase " + e.phases(idx) + "; " + e.errorPoints(idx) + " Fehlerpunkte\n")
      }
      b.toString
    }
    resizable = true
    title = "Spielende"
    headerText = "Spieler " + e.winningPlayer + " hat gewonnen"
    contentText = buildString()
  }.showAndWait()

  content = createField(controller.getGameData._1, controller.getGameData._2, Some(newCardInitial))

  def update(e:OutputEvent) =
    def r = controller.getGameData._1
    def t = controller.getGameData._2
    e match
      case e1: GoToInjectEvent =>
        selectedPlayerToInject = -1
        selected_stash_to_inject = -1
        selected_position_to_inject = -1
        selectedPlayerCard = -1
        mode = INJECT
        content = createField(r, t, None)
      case e2: GoToDiscardEvent =>
        mode = DISCARD
        content = createField(r, t, None)
      case e3: TurnEndedEvent =>
        if(!e3.success)
          moveUnvalidDialog()
        listToSelect.clear()
        selectedPlayerCard = -1
        selectNewOrOpenCard = -1
        mode = SWITCH
        content = createField(r, t, Some(e3.newCard))
      case e4:NewRoundEvent =>
        listToSelect.clear()
        selectedPlayerCard = -1
        selectNewOrOpenCard = -1
        mode = SWITCH
        show_new_Round_Dialog(players, r, t)
        content = createField(r, t, Some(e4.newCard))

  private class SpaceRectangle(val player:Int, val stash:Int, val position:Int) extends Rectangle {
    fill = Color.SaddleBrown
    arcWidth = cardWidth / 4.5
    arcHeight = cardWidth / 4.5
    height = Utils.cardWidth * Utils.cardProportion
    width = Utils.cardWidth / Utils.space_between_cardstashes
    onMouseClicked = e => {
      selectedPlayerToInject = player
      selected_stash_to_inject = stash
      selected_position_to_inject = position
    }
  }
}
