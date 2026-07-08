package utils

trait Observer:
  def update(e:OutputEvent): String

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def remove(s: Observer): Seq[Observer] = subscribers.filterNot(o => o == s)
  def notifyObservers(e:OutputEvent): Unit = subscribers.foreach(o => o.update(e))