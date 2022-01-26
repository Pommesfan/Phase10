package model.FileIoXmlkImplement

import controller.GameRunningControllerStateInterface
import model.{Card, FileIoInterface, RoundData, TurnData}

import java.io.PrintWriter

class FileIoXml extends FileIoInterface:
  def save(state: GameRunningControllerStateInterface): Unit =
    val pw = new PrintWriter("C:\\Users\\jo391wir\\Desktop\\Hallo.xml")
    pw.write(toXML(state).toString())
    pw.close()

  def load:GameRunningControllerStateInterface = new GameRunningControllerStateInterface {
    override val players: List[String] = null
    override val r: RoundData = null
    override val t: TurnData = null
  }

  private def cardToXML(c:Card) =
    <color> {c.color} </color>
    <value> {c.value} </value>

  private def toXML(state:GameRunningControllerStateInterface) =
    def r = state.r
    def t = state.t

    <players type="array"> {
      state.players.map(s =>
        <value> {s} </value>)
      } </players>
    <RoundData>
      <numberOfPhase type="array">
        {r.validators.map(v =>
          <value>
            {v.getNumberOfPhase()}
          </value>)}
      </numberOfPhase>
      <errorPoints type="array">
        {r.errorPoints.map(e =>
          <value> {e}  </value> )}
      </errorPoints>
    </RoundData>
    <TurnData>
      <currentPlayer> {t.current_player} </currentPlayer>
      <openCard>
        {cardToXML(t.openCard)}
      </openCard>
    </TurnData>

