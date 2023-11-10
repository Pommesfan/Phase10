package aview.gui.Scenes

import controller.ControllerBaseImplement.CreatePlayerCommand
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import controller.ControllerInterface
import utils.DoCreatePlayerEvent

class StartScreen(controller: ControllerInterface) extends Scene {
  content = new VBox {
    val input = new TextField()

    def startGame(input:String) = controller.solve(
      new DoCreatePlayerEvent(input.split(" ").toList))

    input.setOnKeyTyped(e => if(e.getCharacter == "\r") startGame(input.getText))

    padding = Insets(20,20,20,20)
    spacing = 20
    children = Seq(
      new Label("Spielernamen eingeben"),
      input,
      new Button("Spielen") {
        onAction = e => startGame(input.getText)
      }
    )
    alignment = Pos.CENTER
  }
}
