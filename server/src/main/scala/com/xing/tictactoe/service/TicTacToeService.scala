package com.xing.tictactoe.service

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class TicTacToeService() extends Http4sDsl[IO] {

  val service: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      // Calculate a move
      case req@POST -> Root =>
        ???
    }
  }
}
