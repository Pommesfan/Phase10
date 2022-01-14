import com.google.inject.name.Names
import com.google.inject.TypeLiteral
import com.google.inject.AbstractModule
import com.google.inject.binder.AnnotatedBindingBuilder
import net.codingwell.scalaguice.ScalaModule
import controller.ControllerInterface
import controller.ControllerBaseImplement.Controller

class Phase10Module extends AbstractModule {
  override def configure(): Unit = {
    bind[ControllerInterface](new TypeLiteral[ControllerInterface]() {}).to(classOf[Controller])
  }
}
