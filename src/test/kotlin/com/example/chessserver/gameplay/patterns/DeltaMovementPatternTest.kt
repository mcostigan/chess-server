package com.example.chessserver.gameplay.patterns

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.move.MoveFactory
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class DeltaMovementPatternTest {
    private lateinit var board: IBoard
    private lateinit var moveFactory: MoveFactory
    private lateinit var checkDetectionService: CheckDetectionService

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
    fun `delta movement pattern can only move up to its range`() {
        class DeltaImpl : DeltaMovementPattern(1, 0, 2, moveFactory, true)

        whenever(board.getPiece(any())).thenReturn(null)

        val pattern = DeltaImpl()
        val moves = pattern.availableMoves(board, PieceColor.WHITE, Pair(0, 0))

        assert(moves.size == 2)

    }

    @Test
    fun `delta movement pattern can only move until it encounters piece of same color`() {
        class DeltaImpl : DeltaMovementPattern(1, 0, 8, moveFactory, true)

        whenever(board.getPiece(any())).thenReturn(null)
        val mockTeammate : Piece = mock()
        whenever(mockTeammate.getColor()).thenReturn(PieceColor.WHITE)

        whenever(board.getPiece(Pair(6,0))).thenReturn(mockTeammate)

        val pattern = DeltaImpl()
        val moves = pattern.availableMoves(board, PieceColor.WHITE, Pair(0, 0))

        assert(moves.size == 5)

    }

    @Test
    fun `delta movement pattern can only move until it encounters a foe if canAttack is false`() {
        class DeltaImpl : DeltaMovementPattern(1, 0, 8, moveFactory, false)

        whenever(board.getPiece(any())).thenReturn(null)
        val mockEnemy : Piece = mock()
        whenever(mockEnemy.getColor()).thenReturn(PieceColor.BLACK)

        whenever(board.getPiece(Pair(6,0))).thenReturn(mockEnemy)

        val pattern = DeltaImpl()
        val moves = pattern.availableMoves(board, PieceColor.WHITE, Pair(0, 0))

        assert(moves.size == 5)

    }

    @Test
    fun `delta movement pattern can attack`() {
        class DeltaImpl : DeltaMovementPattern(1, 0, 8, moveFactory, true)

        whenever(board.getPiece(any())).thenReturn(null)
        val mockEnemy : Piece = mock()
        whenever(mockEnemy.getColor()).thenReturn(PieceColor.BLACK)

        whenever(board.getPiece(Pair(6,0))).thenReturn(mockEnemy)

        val pattern = DeltaImpl()
        val moves = pattern.availableMoves(board, PieceColor.WHITE, Pair(0, 0))

        assert(moves.size == 6)

    }

    @Test
    fun `delta movement pattern cannot move outside of board size`() {
        class DeltaImpl : DeltaMovementPattern(1, 0, 8, moveFactory, true)

        whenever(board.getPiece(any())).thenReturn(null)

        val pattern = DeltaImpl()
        val moves = pattern.availableMoves(board, PieceColor.WHITE, Pair(5, 0))

        assert(moves.size == 2)

    }
}