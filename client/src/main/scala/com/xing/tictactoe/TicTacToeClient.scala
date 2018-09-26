package com.xing.tictactoe

import cats.effect.IO

object TicTacToeClient extends App {
  val program = for {
    _ <- IO(println("Please implement me!"))
  } yield ()

  program.unsafeRunSync()
}
