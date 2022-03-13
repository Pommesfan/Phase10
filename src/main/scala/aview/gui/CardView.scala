package aview.gui

import model.Card
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import utils.Utils.{IndexListener, NumberSizeProportion, cardProportion, cardWidth}

class CardView(card:Card, indexListener: Option[IndexListener]) extends Canvas {
  val cardHeight = cardWidth * cardProportion
  val cardArc = cardWidth / 4.5
  val NumberSize = NumberSizeProportion * cardWidth

  height = cardHeight
  width = cardWidth

  if (!indexListener.isEmpty) onMouseClicked = e => indexListener.get.onListen(indexListener.get.index)

  val gc = graphicsContext2D
  gc.setFill(Color.Transparent)
  gc.fillRect(0, 0, cardWidth, cardHeight)
  gc.setFill(Color.White)
  gc.setStroke(Color.Black)
  gc.fillRoundRect(0.0, 0.0, cardWidth, cardHeight, cardArc, cardArc)

  val cardColor = card.color match {
    case 1 => Color.Red
    case 2 => Color.Yellow
    case 3 => Color.Blue
    case 4 => Color.Green
  }

  gc.setFill(cardColor)
  gc.setFont(new Font("Arial", NumberSize))
  gc.fillText(card.value.toString, cardWidth / 4.5, cardWidth, cardWidth / 1.5)
}
