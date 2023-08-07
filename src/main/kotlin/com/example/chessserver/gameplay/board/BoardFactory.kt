package com.example.chessserver.gameplay.board

import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.MoveExecutor
import com.example.chessserver.gameplay.pieces.PieceFactory
import org.springframework.stereotype.Component

@Component
class BoardFactory(
    private val moveExecutor: MoveExecutor,
    private val pieceFactory: PieceFactory,
    private val checkDetectionService: CheckDetectionService
) {
    /**
     * Creates a new instance of board
     */
    fun get(): IBoard {
        // use composition to give board the ability to execute moves, create pieces, and detect check
        // TODO: extract static method to factory
        return GameBoard.newBoard(moveExecutor, pieceFactory, checkDetectionService)
    }

}