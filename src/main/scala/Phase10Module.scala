package scala

import com.google.inject.name.Names
import com.google.inject.TypeLiteral
import com.google.inject.AbstractModule
import com.google.inject.binder.AnnotatedBindingBuilder
import net.codingwell.scalaguice.ScalaModule
import controller.{ControllerInterface, ValidatorFactoryInterface}
import controller.ControllerBaseImplement.Controller
import controller.ValidatorBaseImplement.ValidatorFactory
import model.FileIoInterface
import model.JsonImplement.FileIoJson

class Phase10Module extends AbstractModule {
  override def configure(): Unit = {
    bind[ControllerInterface](new TypeLiteral[ControllerInterface]() {}).to(classOf[Controller])
    bind[ValidatorFactoryInterface](new TypeLiteral[ValidatorFactoryInterface]() {}).to(classOf[ValidatorFactory])
    bind[FileIoInterface](new TypeLiteral[FileIoInterface]() {}).to(classOf[FileIoJson])
  }
}
