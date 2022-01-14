package aview

import scalafx.application.JFXApp3
import JFXApp3.PrimaryStage
import scalafx.scene.Scene
import aview.Scenes.{PlayingField, StartScreen}
import controller.ControllerInterface
import utils.{GameStartedEvent, Observer, OutputEvent, ProgramStartedEvent}

class GUI(controller: ControllerInterface) extends JFXApp3 with Observer:
  private var playingField:PlayingField = null
  def activate() =
    controller.add(this)
    new Thread {
      override def run(): Unit = main(Array(""))
    }.start()

  override def start(): Unit =
    stage = new PrimaryStage:
      title = "Phase 10"
      scene = new StartScreen(controller)
      onCloseRequest = _ => System.exit(0)

  override def update(e: OutputEvent): String =
    e match {
      case _:ProgramStartedEvent =>
      case _:GameStartedEvent =>
        playingField = new PlayingField(controller, e.asInstanceOf[GameStartedEvent].newCard)
        stage.setScene(playingField)
      case _ => playingField.update(e)
    }
    ""