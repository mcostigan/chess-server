package com.example.chessserver.gameplay.patterns

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.move.MoveFactory
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import java.lang.Integer.min

/**
 * A pattern that defines movements available to a piece, ie Horizontal, Vertical Diagonal
 */
interface MovementPattern {
    /**
     * Given the state of the board and the piece possessing the movement pattern, returns a collection of valid moves.
     *
     * However, these moves may expose the king to check.
     */
    fun availableMoves(
        board: IBoard,
        color: PieceColor,
        position: Pair<Int, Int>
    ): Collection<Move>
}

/**
 * Defines a movement pattern structure as a series of iterations where the row and col changes by `rowDelta` and `colDelta`, respectively.
 */
open class DeltaMovementPattern(
    private val rowDelta: Int,
    private val colDelta: Int,
    private val range: Int,
    private val moveFactory: MoveFactory,
    private val canAttack: Boolean = true,
) :
    MovementPattern {

    /**
     * Finds all available moves along the movement pattern. The pattern continues until one of these conditions occurs:
     *  - The movement pattern encounters the edge of the board
     *  - The movement pattern encounters the end of its `range`
     *  - The movement pattern runs into another piece
     *      - If that piece is an enemy piece and the patter `canAttack`, then the piece may occupy that space but not move past it
     */
    override fun availableMoves(
        board: IBoard,
        color: PieceColor,
        position: Pair<Int, Int>
    ): Collection<Move> {
        // account for white pieces moving "up" the board, and black pieces moving "down" the board
        val vectorizedRowDelta = color.forwardDirection * rowDelta

        val moves = mutableListOf<Move>()
        var row = position.first + vectorizedRowDelta
        var col = position.second + colDelta
        var iterations = 1

        while (row > -1 && row < board.getSize() && col > -1 && col < board.getSize() && iterations <= range) {
            val newPosition = Pair(row, col)
            val occupyingPiece = board.getPiece(newPosition)

            when (true) {
                occupyingPiece == null -> moves.addAll(
                    moveFactory.getMove(position, newPosition, board)
                )
                occupyingPiece.getColor() != color && canAttack -> {
                    moves.addAll(
                        moveFactory.getMove(position, newPosition, board)
                    )
                    break
                }
                else -> break
            }


            row += vectorizedRowDelta
            col += colDelta
            iterations += 1
        }
        return moves
    }
}

/**
 * A movement pattern containing a collection of sub-patterns, ie a vertical pattern is made up of forward and backward patterns
 */
abstract class CompositeMovementPattern(private val subPatterns: Collection<MovementPattern>) :
    MovementPattern {
    override fun availableMoves(
        board: IBoard,
        color: PieceColor,
        position: Pair<Int, Int>
    ): Collection<Move> =
        subPatterns.flatMap { it.availableMoves(board, color, position) }

}

class ForwardMovementPattern(
    range: Int,
    moveFactory: MoveFactory,
    canAttack: Boolean = true
) :
    DeltaMovementPattern(1, 0, range, moveFactory, canAttack)

class BackwardMovementPattern(range: Int, moveFactory: MoveFactory) :
    DeltaMovementPattern(-1, 0, range, moveFactory)

class VerticalMovementPattern(range: Int, moveFactory: MoveFactory) :
    CompositeMovementPattern(
        listOf(
            ForwardMovementPattern(range, moveFactory),
            BackwardMovementPattern(range, moveFactory)
        )
    )

private class RightMovementPattern(range: Int, moveFactory: MoveFactory) :
    DeltaMovementPattern(0, 1, range, moveFactory)

private class LeftMovementPattern(range: Int, moveFactory: MoveFactory) :
    DeltaMovementPattern(0, -1, range, moveFactory)

class HorizontalMovementPattern(range: Int, moveFactory: MoveFactory) :
    CompositeMovementPattern(
        listOf(
            LeftMovementPattern(range, moveFactory),
            RightMovementPattern(range, moveFactory)
        )
    )

private class ForwardRightDiagonal(range: Int, moveFactory: MoveFactory) :
    DeltaMovementPattern(1, 1, range, moveFactory)

private class BackwardRightDiagonal(
    range: Int,
    moveFactory: MoveFactory
) :
    DeltaMovementPattern(-1, 1, range, moveFactory)

private class ForwardLeftDiagonal(range: Int, moveFactory: MoveFactory) :
    DeltaMovementPattern(1, -1, range, moveFactory)

private class BackWardLeftDiagonal(range: Int, moveFactory: MoveFactory) :
    DeltaMovementPattern(-1, -1, range, moveFactory)

open class DiagonalMovementPattern(range: Int, moveFactory: MoveFactory) :
    CompositeMovementPattern(
        listOf(
            ForwardLeftDiagonal(range, moveFactory),
            ForwardRightDiagonal(range, moveFactory),
            BackwardRightDiagonal(range, moveFactory),
            BackWardLeftDiagonal(range, moveFactory)
        )
    )

/**
 * Can move if the square is within a distance of `range` and the move is an attack
 */
class AttackDiagonalMovementPattern(
    range: Int,
    moveFactory: MoveFactory
) :
    CompositeMovementPattern(
        listOf(
            ForwardLeftDiagonal(range, moveFactory),
            ForwardRightDiagonal(range, moveFactory)
        )
    ) {
    override fun availableMoves(
        board: IBoard,
        color: PieceColor,
        position: Pair<Int, Int>
    ): Collection<Move> {
        return super.availableMoves(board, color, position).filter { it.isAttack() }
    }
}


private class LongFwdRightLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(2, 1, 1, moveFactory)

private class LongBackRightLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(-2, 1, 1, moveFactory)

private class LongFwdLeftLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(2, -1, 1, moveFactory)

private class LongBackLeftLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(-2, -1, 1, moveFactory)

private class ShortFwdRightLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(1, 2, 1, moveFactory)

private class ShortBackRightLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(-1, 2, 1, moveFactory)

private class ShortFwdLeftLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(1, -2, 1, moveFactory)

private class ShortBackLeftLShapeMovementPattern(moveFactory: MoveFactory) :
    DeltaMovementPattern(-1, -2, 1, moveFactory)

class LShapeMovementPattern(moveFactory: MoveFactory) :
    CompositeMovementPattern(
        listOf(
            LongBackLeftLShapeMovementPattern(moveFactory),
            LongFwdLeftLShapeMovementPattern(moveFactory),
            LongBackRightLShapeMovementPattern(moveFactory),
            LongFwdRightLShapeMovementPattern(moveFactory),
            ShortBackLeftLShapeMovementPattern(moveFactory),
            ShortFwdLeftLShapeMovementPattern(moveFactory),
            ShortBackRightLShapeMovementPattern(moveFactory),
            ShortFwdRightLShapeMovementPattern(moveFactory)
        )
    )

class CastleMovementPattern(
    private val kingSideRook: Piece,
    private val queenSideRook: Piece,
    private val moveFactory: MoveFactory
) :
    MovementPattern {
    override fun availableMoves(
        board: IBoard,
        color: PieceColor,
        position: Pair<Int, Int>
    ): Collection<Move> {
        val moves = mutableListOf<Move>()

        // get unmoved rooks
        // check if there is a clear path
        val row = position.first
        for (rook in listOf(queenSideRook, kingSideRook).filter { !it.hasMoved() }) {
            val rookCol = rook.getPosition().second
            val kingCol = position.second
            val range = Pair(min(rookCol, kingCol) + 1, rookCol.coerceAtLeast(kingCol) - 1)
            var clearPath = true
            for (col in range.first..range.second) {
                if (board.getPiece(Pair(row, col)) != null) {
                    clearPath = false
                    break
                }
            }
            if (clearPath) {
                moves.add(moveFactory.getMove(position, rook, board))
            }

        }

        return moves
    }
}