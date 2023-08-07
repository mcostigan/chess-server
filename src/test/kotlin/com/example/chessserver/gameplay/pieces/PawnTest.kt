package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.MoveFactory
import com.example.chessserver.gameplay.patterns.MovementPatternFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class PawnTest {
    private val pieceFactory: PieceFactory = mock()
    private val checkDetectionService: CheckDetectionService = mock()
    private val movementPatternFactory: MovementPatternFactory = MovementPatternFactory(MoveFactory())
    private val board: IBoard = mock()

    @BeforeEach
    fun mockMethods() {
        whenever(board.getSize()).thenReturn(8)
        whenever(checkDetectionService.isCheck(any(), any())).thenReturn(false)
    }

    @Test
    fun `can move two square on first move`() {
        whenever(board.getPiece(any())).thenReturn(null)
        val pawn = Pawn(Pair(0, 0), PieceColor.WHITE, pieceFactory, movementPatternFactory)
        whenever(board.getPiece(Pair(0, 0))).thenReturn(pawn)
        val moves = pawn.getAvailableMoves(board)
        assert(moves.size == 2)
        assert(moves.find { it.getTo() == Pair(1, 0) } != null)
        assert(moves.find { it.getTo() == Pair(2, 0) } != null)
    }

    @Test
    fun `can move one square on subsequent move`() {
        whenever(board.getPiece(any())).thenReturn(null)
        val pawn = Pawn(Pair(0, 0), PieceColor.WHITE, pieceFactory, movementPatternFactory)
        pawn.move(Pair(1, 0))
        whenever(board.getPiece(Pair(1, 0))).thenReturn(pawn)
        val moves = pawn.getAvailableMoves(board)
        assert(moves.size == 1)
        assert(moves.find { it.getTo() == Pair(2, 0) } != null)
    }

    @Test
    fun `has queen attributes after promotion`() {
        whenever(board.getPiece(any())).thenReturn(null)
        whenever(pieceFactory.get(eq(GamePiece.QUEEN), any(), any())).thenReturn(
            Queen(Pair(7, 0), PieceColor.WHITE, movementPatternFactory)
        )
        val pawn = Pawn(Pair(0, 1), PieceColor.WHITE, pieceFactory, movementPatternFactory)
        pawn.move(Pair(7, 0))
        pawn.promote(GamePiece.QUEEN)
        assert(pawn.getType() == GamePiece.QUEEN)

        whenever(board.getPiece(Pair(7, 0))).thenReturn(pawn)

        val moves = pawn.getAvailableMoves(board)
        assert(moves.size == 21)
    }
}