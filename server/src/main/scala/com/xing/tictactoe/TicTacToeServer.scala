package com.xing.tictactoe

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import com.xing.tictactoe.service.{TicTacToeService, UserService}
import com.xing.tictactoe.service.User
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  val usersRef =
    Ref.of[IO, Map[String, User]](Map("horst" -> User("horst", "test")))

  def userService(ref: Ref[IO, Map[String, User]]) =
    new UserService(ref).service

  def tictactoeService() = new TicTacToeService().service

  def run(args: List[String]): IO[ExitCode] = {
    def runServer(ref: Ref[IO, Map[String, User]]) = {
      val httpApp = Router("/users" -> userService(ref),
                           "/tictactoe" -> tictactoeService()).orNotFound

      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(httpApp)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }

    IO.contextShift(global)

    for {
      ref <- usersRef
      exitCode <- runServer(ref)
    } yield exitCode
  }
}
