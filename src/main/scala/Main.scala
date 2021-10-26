import java.util.Scanner

@main def hello: Unit =
  println("I am cardgame Phase10!")
  println(msg)

  val s = new Scanner(System.in)
  val players = s.nextLine().split(" ")
  var current_player = 0

  while(true)
    println("Aktueller Spieler: " + players(current_player))
    println("Auszutauschende Karte angeben")
    Thread.sleep(1000)

    current_player = nextPlayer(current_player, players.length)

def msg = "I was compiled by Scala 3. :)"

private def nextPlayer(currentPlayer:Int, numberOfPlayers:Int):Int = (currentPlayer + 1) % numberOfPlayers