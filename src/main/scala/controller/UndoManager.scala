package controller

import controller.Command
import utils.{OutputEvent, ProgramStartedEvent}

class UndoManager[C <: ControllerInterface]:
  private var undoStack: List[Command[C]] = Nil
  private var redoStack: List[Command[C]] = Nil

  def doStep(command: Command[C], c:C):(ControllerStateInterface, OutputEvent) =
    undoStack = command :: undoStack
    command.doStep(c)

  def undoStep(c:C):(ControllerStateInterface, OutputEvent) =
    undoStack match
      case Nil => (c.getInitialState(), new ProgramStartedEvent)
      case head :: stack =>
        val res = head.undoStep(c)
        undoStack = stack
        redoStack = head :: redoStack
        res
