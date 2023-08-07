package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.patterns.MovementPattern

enum class PieceColor(val homeRow: Int, val pawnRow: Int, val forwardDirection: Int) {
    WHITE(0, 1, 1),
    BLACK(7, 6, -1);

    /**
     * Returns the opposing team of a given color
     */
    fun opposite(): PieceColor {
        return if (this == WHITE) {
            BLACK
        } else {
            WHITE
        }
    }
}

abstract class Piece(
    private var position: Pair<Int, Int>,
    private val color: PieceColor,
    private var movementPatterns: Collection<MovementPattern>

) {
    protected var hasMoved = false
    private var isAlive = true


    /**
     * Get all valid moves a piece can make given the current state of the board.
     *
     * However, moves provided from this method may expose the king to check.
     */
    open fun getAvailableMoves(board: IBoard): Collection<Move> {
        val result = mutableListOf<Move>()
        getMovementPatterns().forEach {
            result.addAll(it.availableMoves(board, color, position))
        }
        return result
    }

    /**
     * Get movement patterns a piece can execute
     */
    open fun getMovementPatterns() = movementPatterns

    /**
     * Update the position of a piece to the given square
     */
    open fun move(position: Pair<Int, Int>) {
        hasMoved = true
        this.position = position
    }

    fun hasMoved() = hasMoved

    fun getPosition() = position

    fun kill() {
        this.position = Pair(-1, -1)
        this.isAlive = false
    }

    fun isAlive() = this.isAlive

    fun getColor() = color

    fun sameTeam(otherPiece: Piece) = color == otherPiece.color

    abstract fun getType(): GamePiece

    companion object {
        /**
         * Returns an array of game pieces to which a pawn can promote
         */
        fun getPromotionTargets(): Collection<GamePiece> =
            listOf(GamePiece.QUEEN, GamePiece.KNIGHT, GamePiece.ROOK, GamePiece.BISHOP)
    }
}

interface Promotable {
    /**
     * Update the instance's type and movemnt patters to that of the given `type`
     */
    fun promote(type: GamePiece)

    /**
     * Checks if the piece is eligible for promotion. Pieces that have already promoted are not eligible
     */
    fun canPromote(): Boolean
}

enum class GamePiece(internal val type: Class<out Piece>) {
    QUEEN(Queen::class.java),
    KING(King::class.java),
    BISHOP(Bishop::class.java),
    KNIGHT(Knight::class.java),
    ROOK(Rook::class.java),
    PAWN(Pawn::class.java)
}