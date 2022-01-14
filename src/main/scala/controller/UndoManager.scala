package controller

import controller.Command
import utils.{OutputEvent, ProgramStartedEvent}

class UndoManager:
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command, c:ControllerInterface):(ControllerStateInterface, OutputEvent) =
    undoStack = command :: undoStack
    command.doStep(c)

  def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent) =
    undoStack match
      case Nil => (c.getInitialState(), new ProgramStartedEvent)
      case head :: stack =>
        val res = head.undoStep(c)
        undoStack = stack
        redoStack = head :: redoStack
        res
