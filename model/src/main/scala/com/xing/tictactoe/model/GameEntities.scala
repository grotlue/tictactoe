package com.xing.tictactoe.model

object GameEntities {
  type Field = Map[Int, Map[Int, GameSymbol]]

  /**
    * Checks whether a specified cell is already set.
    */
  def isSet(field: Field, rowIndex: Int, colIndex: Int): Boolean =
    field.get(rowIndex).flatMap(_.get(colIndex)).isDefined

  /**
    * Apply a given `Move` and return a new `Field` with the updated state.
    *
    * Optional TODO: Check whether the given `Move` is even valid.
    */
  def doMove(field: Field, move: Move): Field = {
    if (isSet(field, move.row, move.col)) {
      field
    } else {
      val row = field.getOrElse(move.row, Map.empty)

      field.updated(move.row, row.updated(move.col, move.symbol))
    }
  }

  /**
    * Check whether the whole `Field` is set, i.e. not a single cell is empty.
    */
  def isFull(field: Field, rows: Int = 3, cols: Int = 3): Boolean = field.size == cols && {
    field.values.forall(_.size == rows)
  }

  /**
    * Print the given `Field`. This can be used for debugging.
    */
  def print(field: Field): Unit = {
    val fieldAsString = ((0 to 2) map { rowIndex =>
      field.get(rowIndex).fold(" | | ") { row =>
        ((0 to 2) map { colIndex =>
          row.get(colIndex).getOrElse(" ")
        }).mkString("|")
      }
    }).mkString("\n-----\n")

    println(fieldAsString)
  }

  sealed trait GameSymbol

  object GameSymbol {
    case object O extends GameSymbol
    case object X extends GameSymbol
  }

  final case class Move(symbol: GameSymbol, row: Int, col: Int)
}
