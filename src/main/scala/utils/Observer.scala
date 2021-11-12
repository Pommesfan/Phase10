package utils

trait Observer:
  def update: Unit

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers :+ s
  def remove(s: Observer) = subscribers.filterNot(o => o == s)
  def notifyObservers = subscribers.foreach(o => o.update)