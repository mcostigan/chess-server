package com.example.chessserver.gameplay.patterns

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.move.MoveFactory
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class AttackDiagonalMovementPatternTest {
    private lateinit var checkDetectionService: CheckDetectionService
    private lateinit var board: IBoard
    private lateinit var moveFactory: MoveFactory

    @BeforeEach
    fun mockObjects() {
        board = mock()
        whenever(board.getSize()).thenReturn(8)

        moveFactory = mock()
        whenever(moveFactory.getMove(any(), any<Pair<Int, Int>>(), any())).thenReturn(listOf(mock()))

        checkDetectionService = mock()
        whenever(checkDetectionService.isCheck(any(), any())).thenReturn(false)
    }

    @Test
    fun `cannot move diagonal if no enemy piece is there`() {
        val attackDiagonal = AttackDiagonalMovementPattern(1, moveFactory)

        val mockPiece: Piece = mock()
        whenever(mockPiece.getColor()).thenReturn(PieceColor.WHITE)

        whenever(board.getPiece(any())).thenReturn(null)
        whenever(board.getPiece(Pair(1,1))).thenReturn(mockPiece)
        val moves = attackDiagonal.availableMoves(board, PieceColor.WHITE ,Pair(0,0))
        assert(moves.isEmpty())
    }

    @Test
    fun `can move diagonal if enemy piece is there`() {
        val attackDiagonal = AttackDiagonalMovementPattern(1, moveFactory)

        val attackedPiece: Piece = mock()

        whenever(board.getPiece(any())).thenReturn(null)
        whenever(board.getPiece(Pair(1,1))).thenReturn(attackedPiece)

        val mockMove : Move = mock()
        whenever(mockMove.isAttack()).thenReturn(true)
        whenever(moveFactory.getMove(any(), any<Pair<Int, Int>>(), any())).thenReturn(listOf(mockMove))

        val moves = attackDiagonal.availableMoves(board, PieceColor.WHITE ,Pair(0,0))
        assert(moves.size == 1)
    }
}