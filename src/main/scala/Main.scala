import controller.Controller

import java.util.Scanner
import scala.util.Random
import utils.{GameStartedEvent, Utils}
import model.Card
import aview.TUI

object Main {
  @main def hello: Unit =
    println("I am cardgame Phase10!")
    println(msg)

    val controller = new Controller
    val tui = new TUI(controller)
    tui.start()
    controller.notifyObservers(new GameStartedEvent)

  def msg = "I was compiled by Scala 3. :)"
}

