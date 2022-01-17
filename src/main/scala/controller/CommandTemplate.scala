package controller

import utils.OutputEvent

trait Command[C <: ControllerInterface]:
  def doStep(c:C):(ControllerStateInterface, OutputEvent)
  def undoStep(c:C):(ControllerStateInterface, OutputEvent)