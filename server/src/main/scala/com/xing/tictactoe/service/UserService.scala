package com.xing.tictactoe.service

import cats.effect.IO
import io.circe.Json
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class UserService() extends Http4sDsl[IO] {
  val service: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      // Get a list of all users
      case GET -> Root =>
        Ok(Json.obj("msg" -> "call was successful".asJson))
    }
  }
}
