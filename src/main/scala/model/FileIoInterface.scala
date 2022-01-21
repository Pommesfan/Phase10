package model

import controller.GameRunningControllerStateInterface

trait FileIoInterface {
  def save(state: GameRunningControllerStateInterface): Unit
  def load: GameRunningControllerStateInterface
}
