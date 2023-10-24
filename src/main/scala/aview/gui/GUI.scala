package aview.gui

import aview.gui.Scenes.{PlayingField, StartScreen}
import controller.ControllerInterface
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import utils.{GameEndedEvent, GameStartedEvent, Observer, OutputEvent, ProgramStartedEvent}

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
      //undo from init players
      case _:ProgramStartedEvent =>
        if(playingField != null)
          stage.setScene(StartScreen(controller))
      case _:GameStartedEvent =>
        playingField = new PlayingField(controller, e.asInstanceOf[GameStartedEvent].newCard)
        stage.setScene(playingField)
      case e: GameEndedEvent =>
        playingField.show_game_ended_Dialog(e.winningPlayer)
        stage.setScene(StartScreen(controller))
      case _ => playingField.update(e)
    }
    ""