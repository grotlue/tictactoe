package com.xing.tictactoe

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import com.xing.tictactoe.service.{TicTacToeService, UserService}
import com.xing.tictactoe.service.UserTypes._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  val usersRef =
    Ref.of[IO, Users](Map.empty)

  val sessionsRef =
    Ref.of[IO, Sessions](Map.empty)

  def userService(usersRef: UsersRef, sessionsRef: SessionsRef) =
    new UserService(usersRef, sessionsRef).service

  def tictactoeService() = new TicTacToeService().service

  def run(args: List[String]): IO[ExitCode] = {
    def runServer(usersRef: UsersRef, sessionsRef: SessionsRef) = {
      val httpApp = Router("/users" -> userService(usersRef, sessionsRef),
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
      users <- usersRef
      sessions <- sessionsRef
      exitCode <- runServer(users, sessions)
    } yield exitCode
  }
}
