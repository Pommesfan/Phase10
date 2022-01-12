package aview.Scenes

import aview.CardView
import scalafx.scene.{Scene, layout}
import scalafx.scene.paint.Color
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.scene.control.Button
import scalafx.scene.shape.Rectangle
import controller.{Controller, DiscardCommand, DiscardControllerState, InjectCommand, InjectControllerState, NoDiscardCommand, NoInjectCommand, SwitchCardCommand, SwitchCardControllerState}
import model.{Card, TurnData}
import utils.{GameStartedEvent, GoToDiscardEvent, GoToInjectEvent, NewRoundEvent, OutputEvent, TurnEndedEvent, Utils}
import Utils.{INJECT_AFTER, INJECT_TO_FRONT, IndexListener, cardWidth}

import scala.collection.mutable.ListBuffer

class PlayingField(controller: Controller) extends Scene {
  val SWITCH = 2
  val DISCARD = 3
  val INJECT = 4

  private var mode = SWITCH
  private var selectedPlayerCard = -1
  private var selectedPlayerToInject = -1
  private var selected_stash_to_inject = -1
  private var selected_position_to_inject = -1
  private var players = controller.getPlayers()
  private val listToSelect = new ListBuffer[Int]()

  var selectNewOrOpenCard = -1

  def getPlayerCardView(t:TurnData) = t.cardStash(t.current_player).indices.map(idx =>
    new CardView(t.cardStash(t.current_player)(idx), Some(new IndexListener {
      override val index: Int = idx
      override def onListen(index: Int): Unit = if (mode == SWITCH || mode == INJECT) selectedPlayerCard = index else listToSelect.append(index)
  }))).toSeq

  fill = Color.AliceBlue

  private def createField(t:TurnData, newCard:Option[Card]) = new VBox {
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
                      width = 140
                      height = 210
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
                  new Text("Aktueller Spieler: " + players(t.current_player)),
                  new Button("Tauschen") {
                    disable = !(mode == SWITCH)
                    onMouseClicked = e => if(selectedPlayerCard != -1 && selectNewOrOpenCard != -1)controller.solve(new SwitchCardCommand(selectedPlayerCard, selectNewOrOpenCard, controller.getState))
                  },
                  new Button("Ablegen") {
                    disable = !(mode == DISCARD)
                    onMouseClicked = e =>
                      def g = controller.getGameData
                      def r = g._1
                      def t = g._2
                      val groupedCardIndexes = Utils.groupCardIndexes(listToSelect.toList, r.validators(t.current_player).getNumberOfInputs())
                      controller.solve(new DiscardCommand(groupedCardIndexes, controller.getState))
                  },
                  new Button("Anlegen") {
                    disable = !(mode == INJECT)
                    onMouseClicked = e =>
                      if(!(selectedPlayerToInject == -1 || selectedPlayerCard == -1 || selected_stash_to_inject == -1 || selected_position_to_inject == -1))
                        controller.solve(new InjectCommand(selectedPlayerToInject, selectedPlayerCard, selected_stash_to_inject, selected_position_to_inject, controller.getState))
                  },
                  new Button("nächster Spieler") {
                    disable = !(mode == DISCARD || mode == INJECT)
                    onMouseClicked = e => controller.getState match {
                      case _:DiscardControllerState => controller.solve(new NoDiscardCommand(controller.getState))
                      case _:InjectControllerState => controller.solve(new NoInjectCommand(controller.getState))
                    }
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
      showDiscardedCards(t.discardedStash)
    ),
  }

  def showDiscardedCards(stash:List[Option[List[List[Card]]]]): VBox = {
    val vbox = new VBox()
    for(p <- players.indices)
      vbox.getChildren.add(new Text(players(p)))

      def showDiscardedStash():HBox =
        val hbox = new HBox()
        val concreteStash = stash(p).get
        for(s <- concreteStash.indices)
          def cardGroup = concreteStash(s)
          hbox.getChildren.add(new SpaceRectangle(p, s, INJECT_TO_FRONT))
          for(card <- cardGroup)
            hbox.getChildren.add(new CardView(card, None))
          hbox.getChildren.add(new SpaceRectangle(p, s, INJECT_AFTER))
        hbox

      if(stash(p).nonEmpty)
        vbox.getChildren.add(showDiscardedStash())
    vbox
  }

  //get new card when starting this gui
  val newCard = controller.getState.asInstanceOf[SwitchCardControllerState].newCard

  content = createField(controller.getGameData._2, Some(newCard))

  def update(e:OutputEvent) =
    def t = controller.getGameData._2
    e match
      case e1: GoToInjectEvent =>
        selectedPlayerToInject = -1
        selected_stash_to_inject = -1
        selected_position_to_inject = -1
        selectedPlayerCard = -1
        mode = INJECT
        content = createField(t, None)
      case e2: GoToDiscardEvent =>
        mode = DISCARD
        content = createField(t, None)
      case e3: TurnEndedEvent =>
        listToSelect.clear()
        selectedPlayerCard = -1
        selectNewOrOpenCard = -1
        mode = SWITCH
        content = createField(t, Some(e3.newCard))
      case e4:NewRoundEvent =>
        listToSelect.clear()
        selectedPlayerCard = -1
        selectNewOrOpenCard = -1
        mode = SWITCH
        content = createField(t, Some(e4.newCard))

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