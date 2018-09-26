package com.xing.tictactoe.model

import com.xing.tictactoe.model.GameEntities.Field

object TicTacToeMessages {
  final case class MoveRequest(username: String, token: String, field: Field)
  final case class GameResponse(field: Field)
}
