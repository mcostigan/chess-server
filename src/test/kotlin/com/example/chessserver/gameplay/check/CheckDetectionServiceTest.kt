package com.example.chessserver.gameplay.check

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CheckDetectionServiceTest {

    private val board: IBoard = mock()
    private val mockEnemy: Piece = mock()
    private val mockAttackPiece: Piece = mock()
    private val checkDetectionService: CheckDetectionService = CheckDetectionService()


    @Test
    fun `if enemy piece can attack king, returns true`() {
        mockMethods(true)
        assert(checkDetectionService.isCheck(PieceColor.WHITE, board))
    }

    @Test
    fun `if enemy piece cannot attack king, returns false`() {
        mockMethods(false)
        assert(!checkDetectionService.isCheck(PieceColor.WHITE, board))
    }

    @Test
    fun `if enemy piece can attack target, return true`(){
        mockMethods(false)
        assert(checkDetectionService.canAttack(PieceColor.BLACK, Pair(0,0), board))

    }

    @Test
    fun `if enemy piece cannot attack target, return false`(){
        mockMethods(false)
        assert(!checkDetectionService.canAttack(PieceColor.BLACK, Pair(1,0), board))

    }

    private fun mockMethods(isCheck: Boolean) {
        val mockAttack: Move = mock()
        whenever(mockAttack.isAttack()).thenReturn(true)
        whenever(mockAttack.getAttackedPiece()).thenReturn(mockAttackPiece)
        whenever(mockAttackPiece.getType()).thenReturn(if (isCheck) GamePiece.KING else GamePiece.PAWN)
        whenever(mockAttackPiece.getPosition()).thenReturn(Pair(0,0))


        whenever(board.getPiecesByColor(PieceColor.BLACK)).thenReturn(listOf(mockEnemy))
        whenever(mockEnemy.getAvailableMoves(board)).thenReturn(listOf(mockAttack))
    }
}