package aview.Scenes

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import controller.{ControllerInterface, CreatePlayerCommand}

class StartScreen(controller: ControllerInterface) extends Scene {
  content = new VBox {
    val input = new TextField()

    def startGame(input:String) = controller.solve(
      new CreatePlayerCommand(input.split(" ").toList, controller.getState))

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
