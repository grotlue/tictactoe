package com.xing.tictactoe

import com.xing.tictactoe.model.GameEntities._

import scala.util.Random

object GameLogic {
  private val rand = new Random()

  /**
    * The "AI" will compute apply a `Move` to a given `Field`, if that is still possible.
    *
    * Optional TODO: Improve the "AI". It's terribly dull at the moment.
    */
  def doComputerMove(field: Field): Option[Field] = {
    if (!isFull(field)) {
      val emptyCells = for {
        rowIndex <- 0 to 2
        colIndex <- 0 to 2
        if !isSet(field, rowIndex, colIndex)
      } yield Move(GameSymbol.O, rowIndex, colIndex)

      Some(doMove(field, rand.shuffle(emptyCells.toList).head))
    } else {
      None
    }
  }
}
