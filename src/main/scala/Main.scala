import aview.gui.GUI

import java.util.Scanner
import scala.util.Random
import utils.{ProgramStartedEvent, Utils}
import model.Card
import aview.tui.TUI
import controller.ControllerInterface
import controller.ControllerBaseImplement.Controller
import com.google.inject.Guice

object Main {
  @main def hello: Unit =
    val injector = Guice.createInjector(new Phase10Module)
    println("I am cardgame Phase10!")
    println(msg)

    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = new TUI(controller)
    val gui = new GUI(controller)
    tui.start()
    gui.activate()
    controller.notifyObservers(new ProgramStartedEvent)

  def msg = "I was compiled by Scala 3. :)"
}

