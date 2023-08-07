package com.example.chessserver.gameplay.board

import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.HypotheticalMoveExecutor
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor

/**
 * The hypothetical board is an implementation of `IBoard` that can be reset to the current gameplay board
 * This allows for executing a hypothetical move without changing the actual gameplay board or state of pieces
 */

class HypotheticalBoard(moveExecutor: HypotheticalMoveExecutor, checkDetectionService: CheckDetectionService) :
    Board(
        MutableList(8) { MutableList(8) { null } }, moveExecutor, checkDetectionService
    ) {
    private lateinit var gameplayBoard: IBoard

    // allow easy creation from within another IBoard class
    constructor(gameplayBoard: IBoard, checkDetectionService: CheckDetectionService): this(HypotheticalMoveExecutor(), checkDetectionService){
        setGameplayBoard(gameplayBoard)
    }

    fun setGameplayBoard(board: IBoard){
        this.gameplayBoard = board
        this.reset()
    }

    override fun getPiecesByColor(pieceColor: PieceColor): Collection<Piece> =
        squares.flatten().filterNotNull().filter { it.getColor() == pieceColor }

    override fun exposesKing(move: Move): Boolean {
        // execute the move using the `HypotheticalMoveExecutor`.
        this.move(move)

        // determine if this team is now in check
        var b = this.checkDetectionService.isCheck(move.getColor(), this)

        // a castle move cannot move the king out of, through, or into check
        if (move.isCastle() && !b) {
            val passThrough = Pair(move.getTo().first, if (move.getTo().second == 2) 3 else 5)
            b = b || this.checkDetectionService.canAttack(
                move.getColor().opposite(),
                move.getFrom(),
                this
            ) || this.checkDetectionService.canAttack(move.getColor().opposite(), passThrough, this)
        }

        // reset the board to match the gameplay board
        this.reset()
        return b
    }

    /**
     * Copies the state of the board to the hypothetical board
     */
    fun reset() {
        for (i in 0..7) {
            for (j in 0..7) {
                val piece = gameplayBoard.getPiece(Pair(i, j))
                this.squares[i][j] = piece
            }
        }
    }


}