package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.pieces.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.mockito.verification.VerificationMode

internal open class DefaultMoveExecutorTest {

    private var squares: MutableList<MutableList<Piece?>> = mock()

    protected open var moveExecutor: MoveExecutor = DefaultMoveExecutor()

    protected open var times = times(1)


    @Test
    fun `should update squares and piece when executing basic move`() {
        val piece = mockPiece<Piece>()
        val basicMove = mockBasicMove(piece)

        val mockRow: MutableList<Piece?> = mock()
        mockSquares(squares, mockRow)

        moveExecutor.executeMove(basicMove, squares)

        verifyMove(piece, Pair(0, 1))

        verifySquares(mockRow, piece)
    }

    @Test
    fun `should update squares and piece when executing attack move`() {
        val piece = mockPiece<Piece>()

        // Set up the mock attack move
        val attack: AttackDecorator = mock()

        // set up attacked piece
        val attackedPiece: Piece = mock()
        whenever(attackedPiece.getPosition()).thenReturn(Pair(0, 1))
        whenever(attack.attackedPiece).thenReturn(attackedPiece)

        val basicMove = mockBasicMove(piece)
        whenever(attack.wrapped).thenReturn(basicMove)

        // Mock interaction with squares
        val mockRow: MutableList<Piece?> = mock()
        whenever(squares[0]).thenReturn(mockRow)

        // Execute the move
        moveExecutor.executeMove(attack, squares)

        // Verify that the pieces were moved and killed
        verifyKill(attackedPiece)
        verifyMove(piece, Pair(0, 1))

        // Verify squares are updated
        verifySquares(mockRow, piece)
    }

    @Test
    fun `should update squares and piece when executing promotion move`() {
        val piece = mockPiece<Pawn>()
        val promotion: PromotionDecorator = mock()

        whenever(promotion.toPromote).thenReturn(piece)
        whenever(promotion.promoteTo).thenReturn(GamePiece.QUEEN)

        val basicMove = mockBasicMove(piece)
        whenever(promotion.wrapped).thenReturn(basicMove)

        val mockRow: MutableList<Piece?> = mock()
        whenever(squares[0]).thenReturn(mockRow)

        moveExecutor.executeMove(promotion, squares)

        verifyMove(piece, Pair(0, 1))
        verifyPromote(piece, GamePiece.QUEEN)

        verifySquares(mockRow, piece)
    }

    @Test
    fun `should update squares and piece when executing castle move`() {
        val piece = mockPiece<King>(Pair(0, 4))
        val rook: Rook = mockPiece<Rook>()
        whenever(rook.castle()).thenReturn(Pair(0, 3))

        val basicCastleMove: BasicMove = mockBasicMove(piece)
        whenever(basicCastleMove.endPosition).thenReturn(Pair(0, 2))

        val castle: CastleDecorator = mock()

        whenever(castle.castle).thenReturn(rook)
        whenever(castle.wrapped).thenReturn(basicCastleMove)

        whenever(castle.wrapped).thenReturn(basicCastleMove)

        val mockRow: MutableList<Piece?> = mock()
        whenever(squares[0]).thenReturn(mockRow)

        moveExecutor.executeMove(castle, squares)

        verifyMove(piece, Pair(0, 2))
        verify(rook).castle()

        verify(mockRow)[4] = null
        verify(mockRow)[2] = piece
        verify(mockRow)[3] = rook
        verify(mockRow)[0] = null
    }

    private inline fun <reified T : Piece> mockPiece(position: Pair<Int, Int> = Pair(0, 0)): T {
        val piece: T = mock()
        whenever(piece.getPosition()).thenReturn(position)
        return piece
    }

    private fun mockBasicMove(piece: Piece): BasicMove {
        // Set up the mock BasicMove
        val basicMove: BasicMove = mock()
        whenever(basicMove.piece).thenReturn(piece)
        whenever(basicMove.endPosition).thenReturn(Pair(0, 1))
        return basicMove
    }

    private fun mockSquares(squares: MutableList<MutableList<Piece?>>, mockRow: MutableList<Piece?>) {
        // Mock interaction with squares
        whenever(squares[0]).thenReturn(mockRow)
    }

    private fun verifySquares(mockRow: MutableList<Piece?>, piece: Piece) {
        // Verify squares are updated
        verify(mockRow)[0] = null
        verify(mockRow)[1] = piece
    }

    private fun verifyMove(piece: Piece, position: Pair<Int, Int>) {
        verify(piece, times).move(position)
    }

    private fun verifyKill(piece: Piece) {
        verify(piece, times).kill()
    }

    private fun verifyPromote(piece: Promotable, promoteTo: GamePiece) {
        verify(piece, times).promote(promoteTo)
    }

}


internal class HypotheticalMoveExecutorTest : DefaultMoveExecutorTest() {
    override var moveExecutor: MoveExecutor = HypotheticalMoveExecutor()
    override var times: VerificationMode = never()

}