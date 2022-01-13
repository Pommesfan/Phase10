import java.util.Scanner
import scala.util.Random
import utils.{ProgramStartedEvent, Utils}
import model.Card
import aview.{GUI, TUI}
import controller.ControllerBaseImplement.Controller

object Main {
  @main def hello: Unit =
    println("I am cardgame Phase10!")
    println(msg)

    val controller = new Controller
    val tui = new TUI(controller)
    val gui = new GUI(controller)
    tui.start()
    gui.activate()
    controller.notifyObservers(new ProgramStartedEvent)

  def msg = "I was compiled by Scala 3. :)"
}

