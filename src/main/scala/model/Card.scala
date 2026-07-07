package model

trait Card:
  def errorPoints: Int

case class RegularCard(color:Int, value:Int) extends Card:
  override def toString: String =
    def colorName: String = color match
      case 1 => "Rot"
      case 2 => "Gelb"
      case 3 => "Blau"
      case 4 => "Grün"

    "Farbe: " + colorName + "; Wert = " + value.toString

  override def errorPoints: Int = if (value < 10) 5 else 10

case class JokerCard() extends Card:
  override def toString: String = "Joker"
  override def errorPoints = 25