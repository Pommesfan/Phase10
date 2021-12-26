package controller

import utils.{ProgramStartedEvent, OutputEvent}

class UndoManager:
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command, c:Controller):(ControllerState, OutputEvent) =
    undoStack = command :: undoStack
    command.doStep(c)

  def undoStep(c:Controller):(ControllerState, OutputEvent) =
    undoStack match
      case Nil => (new InitialState, new ProgramStartedEvent)
      case head :: stack =>
        val res = head.undoStep(c)
        undoStack = stack
        redoStack = head :: redoStack
        res
