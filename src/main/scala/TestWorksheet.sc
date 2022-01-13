trait A:
  val a:Int

class B extends A:
  override val a: Int = 8

trait C extends A:
  val b: Int

class D(i:Int) extends C:
  override val a: Int = i
  override val b: Int = i * 2

val d = new D(13)
d.a
d.b

trait E(val f:Int)

trait F(val i:Int) extends E(i)

val f = new F(3)
g.i