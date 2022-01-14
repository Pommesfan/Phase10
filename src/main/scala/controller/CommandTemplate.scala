package controller

import utils.OutputEvent

trait Command:
  def doStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent)
  def undoStep(c:ControllerInterface):(ControllerStateInterface, OutputEvent)