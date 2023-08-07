package com.example.chessserver.gameplay.board

import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.move.MoveExecutor
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor

interface IBoard {
    /**
     * Returns the dimensions of the board
     */
    fun getSize(): Int

    /**
     * Returns the piece at the given rank and file, if one exists
     */
    fun getPiece(position: Pair<Int, Int>): Piece?

    /**
     * Updates the state of the board and its pieces to reflect a move
     */
    fun move(move: Move)

    /**
     * Returns all *live* pieces on a given team
     */
    fun getPiecesByColor(pieceColor: PieceColor): Collection<Piece>

    /**
     * Determine if a move would expose the king to check
     */
    fun exposesKing(move: Move): Boolean

    /**
     * For a team, returns all available moves that do not expose the king to check
     */
    fun availableMoves(team: PieceColor): Collection<Move>

    /**
     * Returns `true` if `team` is currently in check
     */
    fun isCheck(team: PieceColor): Boolean
}

/**
 * An abstract implementation of basic board operations.
 */
abstract class Board(
    protected val squares: MutableList<MutableList<Piece?>>,
    private var moveExecutor: MoveExecutor,
    protected val checkDetectionService: CheckDetectionService
) : IBoard {

    override fun getSize(): Int = 8

    override fun getPiece(position: Pair<Int, Int>): Piece? = this.squares[position.first][position.second]

    override fun move(move: Move) {
        moveExecutor.executeMove(move, this.squares)
    }

    abstract override fun getPiecesByColor(pieceColor: PieceColor): Collection<Piece>

    abstract override fun exposesKing(move: Move): Boolean

    override fun availableMoves(team: PieceColor): Collection<Move> =
        getPiecesByColor(team).flatMap { it.getAvailableMoves(this) }.filter { !this.exposesKing(it) }

    override fun isCheck(team: PieceColor): Boolean = checkDetectionService.isCheck(team, this)

}


