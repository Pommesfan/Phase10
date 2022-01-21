package model.JsonImplement

import controller.GameRunningControllerStateInterface
import model.{Card, FileIoInterface, RoundData, TurnData}
import play.api.libs.json.*

import java.io.PrintWriter

class FileIoJson extends FileIoInterface {
  private def cardToJSon(c:Card) = JsObject(Seq(
    "color" -> JsNumber(c.color),
    "value" -> JsNumber(c.value)
  ))

  def save(state: GameRunningControllerStateInterface): Unit =
    def r = state.r
    def t = state.t
    val pw = new PrintWriter("C:\\Users\\Johannes\\Desktop\\Hallo.txt")
    val js = JsObject(Seq(
      "players" -> JsArray(state.players.map(p => JsString(p))),
      "RoundData" -> JsObject(Seq(
        "numberofPhase" -> JsArray(r.validators.map(v => JsNumber(v.getNumberOfPhase())).toSeq),
        "errorPoints" -> JsArray(r.errorPoints.map(n => JsNumber(n)).toSeq)
      )),
      "TurnData" -> JsObject(Seq(
        "currentPlayer" -> JsNumber(t.current_player),
        "cardStash" -> JsArray(
          t.cardStash.map(cs => JsArray(
            cs.map(c => cardToJSon(c))
          ))
        ),
        "discardedStash" -> JsArray(
          t.discardedStash.map(o =>
            if(o.nonEmpty)
              JsArray(o.get.map(cs =>
                JsArray(cs.map(c =>
                  cardToJSon(c)
                ))
              ))
            else
              JsNull
          )
        ),
        "openCard" -> cardToJSon(t.openCard)
      ))
    ))
    pw.print(js.toString)
    pw.close()


  def load: GameRunningControllerStateInterface = new GameRunningControllerStateInterface {
    override val players: List[String] = null
    override val r: RoundData = null
    override val t: TurnData = null
  }
}
