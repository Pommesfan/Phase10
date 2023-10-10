package aview.gui

import model.Card
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import utils.Utils.{IndexListener, NumberSizeProportion, cardProportion, cardWidth}

class CardView(card:Card, indexListener: Option[IndexListener]) extends Canvas {
  val CARD_ARC_FACTOR = 4.5
  val WAVE_DEVIATION_FACTOR = 4
  val cardHeight = cardWidth * cardProportion
  val cardArc = cardWidth / CARD_ARC_FACTOR
  val NumberSize = NumberSizeProportion * cardWidth
  val waveDeviation = cardWidth / WAVE_DEVIATION_FACTOR

  height = cardHeight
  width = cardWidth

  if (!indexListener.isEmpty) onMouseClicked = e => indexListener.get.onListen(indexListener.get.index)

  val gc = graphicsContext2D

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
    gc.arcTo(bx, by, ax, ay, 20)
    gc.closePath()
    gc.fill()
  }

  gc.setFill(Color.White)
  gc.fillRect(0, 0, cardWidth, cardHeight)
  gc.setStroke(Color.Black)

  val cardColor = card.color match {
    case 1 => Color.Red
    case 2 => Color.Yellow
    case 3 => Color.Blue
    case 4 => Color.Green
  }

  gc.setFill(cardColor)
  gc.setFont(new Font("Arial", NumberSize))
  gc.fillText(card.value.toString, cardWidth / 4.5, cardWidth + 10, cardWidth / 1.5)

  drawWave(0, 0, 0, waveDeviation, cardWidth, waveDeviation, cardWidth, 0, cardArc)
  drawWave(0, cardHeight, 0, cardHeight - waveDeviation, cardWidth, cardHeight - waveDeviation, cardWidth, cardHeight, cardArc)

  gc.setFill(Color.AliceBlue)
  cutEdge(0, cardArc, 0, 0, cardArc, 0)
  cutEdge(cardWidth, cardArc, cardWidth, 0, cardWidth - cardArc, 0)
  cutEdge(0, cardHeight - cardArc, 0, cardHeight, cardArc, cardHeight)
  cutEdge(cardWidth, cardHeight - cardArc, cardWidth, cardHeight, cardWidth - cardArc, cardHeight)
}
