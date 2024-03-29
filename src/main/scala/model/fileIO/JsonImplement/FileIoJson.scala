package model.fileIO.JsonImplement

import controller.ControllerBaseImplement.{DiscardControllerState, InjectControllerState, SwitchCardControllerState}
import controller.GameRunningControllerStateInterface
import controller.ValidatorBaseImplement.GroupType.Value
import model.fileIO.FileIoInterface
import model.{Card, RoundData, TurnData}
import play.api.libs.json.*

import java.io.PrintWriter

class FileIoJson extends FileIoInterface {
  private object GameState extends Enumeration:
    val SWITCH, DISCARD, INJECT = Value

  private def cardToJSon(c:Card) = JsObject(Seq(
    "color" -> JsNumber(c.color),
    "value" -> JsNumber(c.value)
  ))

  private def getStateNumber(state:GameRunningControllerStateInterface) = state match {
    case _: SwitchCardControllerState => GameState.SWITCH
    case _: DiscardControllerState => GameState.DISCARD
    case _: InjectControllerState => GameState.INJECT
  }

  private def getNewCard(state: GameRunningControllerStateInterface) =
    state match {
      case s:SwitchCardControllerState => cardToJSon(s.newCard)
      case _ => JsNull
    }

  def save(state: GameRunningControllerStateInterface): Unit =
    def r = state.r
    def t = state.t
    val pw = new PrintWriter("C:\\Users\\Johannes\\Desktop\\Hallo.txt")
    val js = JsObject(Seq(
      "state" -> JsNumber(getStateNumber(state).id),
      "players" -> JsArray(state.players.map(p => JsString(p))),
      "RoundData" -> JsObject(Seq(
        "numberofPhase" -> JsArray(r.validators.map(v => JsNumber(v.getNumberOfPhase())).toSeq),
        "errorPoints" -> JsArray(r.errorPoints.map(n => JsNumber(n)).toSeq)
      )),
      "TurnData" -> JsObject(Seq(
        "newCard" -> getNewCard(state),
        "currentPlayer" -> JsNumber(t.current_player),
        "cardStash" -> JsArray(
          t.playerCardDeck.cards.map(cs => JsArray(
            cs.map(c => cardToJSon(c))
          ))
        ),
        "discardedStash" -> JsArray(
          t.discardedCardDeck.cards.map(o =>
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
