package aview.gui

import model.{Card, JokerCard, RegularCard}
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.{DropShadow, Shadow}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import utils.Utils.{IndexListener, CARD_PROPORTION, CARD_WIDTH}

class CardView(card:Card, indexListener: Option[IndexListener]) extends Canvas {
  private val NUMBER_SIZE_PROPORTION = 0.9
  private val ONE_DIGIT_WIDTH_PROPORTION = 0.5
  private val TWO_DIGIT_WIDTH_PROPORTION = 1.2
  private val JOKER_SIZE_PROPORTION = 0.4
  private val JOKER_WIDTH_PROPORTION = 0.8
  private val FONT_TYPE = "Arial"
  private val SHADOW_FACTOR = 4
  private val CARD_ARC_FACTOR = 4.5
  private val CARD_RADIUS_FACTOR = 6
  private val WAVE_DEVIATION_FACTOR = 4
  private val cardHeight = CARD_WIDTH * CARD_PROPORTION
  private val cardArc = CARD_WIDTH / CARD_ARC_FACTOR
  private val waveDeviation = CARD_WIDTH / WAVE_DEVIATION_FACTOR

  height = cardHeight
  width = CARD_WIDTH

  if (indexListener.isDefined) onMouseClicked = e => indexListener.get.onListen(indexListener.get.index)

  private val gc = graphicsContext2D

  private def drawWave(ax: Double, ay: Double, bx: Double, by: Double, cx: Double, cy: Double, dx: Double, dy: Double, w: Double): Unit = {
    gc.beginPath()
    gc.moveTo(bx, by)
    gc.bezierCurveTo(cx/3, by+w, cx/3*2, by, cx, cy-w)
    gc.lineTo(dx, dy)
    gc.lineTo(ax,ay)
    gc.closePath()
    gc.fill()
  }

  private def cutEdge(ax:Double, ay:Double, bx:Double, by:Double, cx:Double, cy:Double): Unit = {
    gc.beginPath()
    gc.moveTo(ax, ay)
    gc.lineTo(bx, by)
    gc.lineTo(cx, cy)
    gc.arcTo(bx, by, ax, ay, CARD_WIDTH / CARD_RADIUS_FACTOR)
    gc.closePath()
    gc.fill()
  }

  private def digitColor = cardColor match {
    case Color.Red => Color.FireBrick
    case Color.Yellow => Color.Gold
    case Color.Blue => Color.DarkBlue
    case Color.Green => Color.DarkGreen
    case Color.Gray => Color.DarkSlateGray
  }

  private def shadowColor = cardColor match {
    case Color.Red => Color.OrangeRed
    case Color.Yellow => Color.LightYellow
    case Color.Blue => Color.DeepSkyBlue
    case Color.Green => Color.LawnGreen
    case Color.Gray => Color.LightGray
  }

  gc.setFill(Color.White)
  gc.fillRect(0, 0, CARD_WIDTH, cardHeight)
  gc.setStroke(Color.Black)
  private val cardColor = card match {
    case c: RegularCard => c.color match {
      case 1 => Color.Red
      case 2 => Color.Yellow
      case 3 => Color.Blue
      case 4 => Color.Green
    }
    case _: JokerCard => Color.Gray
  }


  gc.setFill(digitColor)

  card match
    case c: RegularCard =>
      val numberSize = CARD_WIDTH * NUMBER_SIZE_PROPORTION
      val numberWidth = if(c.value < 10) numberSize * ONE_DIGIT_WIDTH_PROPORTION else numberSize * TWO_DIGIT_WIDTH_PROPORTION
      val posX = (CARD_WIDTH - numberWidth) / 2
      val posY = (cardHeight + numberSize) / 2
      gc.setFont(new Font(FONT_TYPE, numberSize))
      gc.fillText(c.value.toString, posX, posY, numberWidth)
    case _: JokerCard =>
      val fontSize = CARD_WIDTH * JOKER_SIZE_PROPORTION
      val fontWidth = CARD_WIDTH * JOKER_WIDTH_PROPORTION
      val posX = (CARD_WIDTH - fontWidth) / 2
      val posY = (cardHeight + fontSize) / 2
      gc.setFont(new Font(FONT_TYPE, fontSize))
      gc.fillText("Joker", posX, posY, fontWidth)

  gc.setFill(cardColor)
  gc.setEffect(new DropShadow {
    radius = CARD_WIDTH / SHADOW_FACTOR
    color = shadowColor
  })
  drawWave(0, 0, 0, waveDeviation, CARD_WIDTH, waveDeviation, CARD_WIDTH, 0, cardArc)
  drawWave(0, cardHeight, 0, cardHeight - waveDeviation, CARD_WIDTH, cardHeight - waveDeviation, CARD_WIDTH, cardHeight, cardArc)

  gc.setFill(Color.AliceBlue)
  cutEdge(0, cardArc, 0, 0, cardArc, 0)
  cutEdge(CARD_WIDTH, cardArc, CARD_WIDTH, 0, CARD_WIDTH - cardArc, 0)
  cutEdge(0, cardHeight - cardArc, 0, cardHeight, cardArc, cardHeight)
  cutEdge(CARD_WIDTH, cardHeight - cardArc, CARD_WIDTH, cardHeight, CARD_WIDTH - cardArc, cardHeight)
}
