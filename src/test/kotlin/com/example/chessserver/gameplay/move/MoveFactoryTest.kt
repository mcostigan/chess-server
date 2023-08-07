package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Pawn
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class MoveFactoryTest {
    private lateinit var board: IBoard

    private var moveFactory = MoveFactory()

    @BeforeEach
    fun mockBoard() {
        board = mock()
    }

    @Test
    fun `get basic move`() {
        val start = Pair(1, 1)
        val end = Pair(2, 1)

        val mockPiece: Piece = mock()
        whenever(mockPiece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockPiece.getPosition()).thenReturn(start)
        whenever(mockPiece.getType()).thenReturn(GamePiece.PAWN)


        whenever(board.getPiece(start)).thenReturn(mockPiece)
        whenever(board.getPiece(end)).thenReturn(null)

        val move = moveFactory.getMove(start, end, board)

        assert(move.size == 1)
        val firstMove = move[0]
        assert(!firstMove.isAttack())
        assert(!firstMove.isCastle())
        assert(!firstMove.isPromotion())
        assert(firstMove.getColor() == PieceColor.WHITE)
        assert(firstMove.getAttackedPiece() == null)
        assert(firstMove.getTo() == end)
        assert(firstMove.getDescription() == "WHITE PAWN @ $start to $end.")
    }

    @Test
    fun `get attack move`() {
        val start = Pair(1, 1)
        val end = Pair(2, 1)

        val mockPiece: Piece = mock()
        whenever(mockPiece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockPiece.getPosition()).thenReturn(start)
        whenever(mockPiece.getType()).thenReturn(GamePiece.PAWN)

        val attackedPiece: Piece = mock()
        whenever(attackedPiece.getColor()).thenReturn(PieceColor.BLACK)
        whenever(attackedPiece.getPosition()).thenReturn(end)
        whenever(attackedPiece.getType()).thenReturn(GamePiece.PAWN)

        whenever(board.getPiece(start)).thenReturn(mockPiece)
        whenever(board.getPiece(end)).thenReturn(attackedPiece)

        val move = moveFactory.getMove(start, end, board)

        assert(move.size == 1)
        val firstMove = move[0]
        assert(firstMove.isAttack())
        assert(!firstMove.isCastle())
        assert(!firstMove.isPromotion())
        assert(firstMove.getColor() == PieceColor.WHITE)
        assert(firstMove.getAttackedPiece() == attackedPiece)
        assert(firstMove.getTo() == end)
        assert(firstMove.getDescription() == "WHITE PAWN @ $start to $end. Kills BLACK PAWN.")
    }

    @Test
    fun `get promotion move`() {
        val start = Pair(6, 1)
        val end = Pair(7, 1)

        val mockPiece: Pawn = mock()
        whenever(mockPiece.move(any())).thenCallRealMethod()

        whenever(mockPiece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockPiece.getPosition()).thenReturn(start)
        whenever(mockPiece.getType()).thenReturn(GamePiece.PAWN)
        whenever(mockPiece.canPromote()).thenReturn(true)

        whenever(board.getPiece(start)).thenReturn(mockPiece)
        whenever(board.getPiece(end)).thenReturn(null)

        val moves = moveFactory.getMove(start, end, board)

        assert(moves.size == Piece.getPromotionTargets().size)
        moves.forEach {
            assert(!it.isAttack())
            assert(!it.isCastle())
            assert(it.isPromotion())
            assert(it.getAttackedPiece() == null)
            assert(it.getColor() == PieceColor.WHITE)
            assert(it.getTo() == end)
            assert(it.getDescription().startsWith("WHITE PAWN @ $start to $end. Promoted to"))
        }
    }

    @Test
    fun `get attack with promotion`() {
        val start = Pair(6, 1)
        val end = Pair(7, 1)

        val mockPiece: Pawn = mock()
        whenever(mockPiece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(mockPiece.getPosition()).thenReturn(start)
        whenever(mockPiece.getType()).thenReturn(GamePiece.PAWN)
        whenever(mockPiece.canPromote()).thenReturn(true)

        val attackedPiece: Piece = mock()
        whenever(attackedPiece.getColor()).thenReturn(PieceColor.BLACK)
        whenever(attackedPiece.getPosition()).thenReturn(end)
        whenever(attackedPiece.getType()).thenReturn(GamePiece.PAWN)

        whenever(board.getPiece(start)).thenReturn(mockPiece)
        whenever(board.getPiece(end)).thenReturn(attackedPiece)

        val moves = moveFactory.getMove(start, end, board)

        assert(moves.size == Piece.getPromotionTargets().size)
        moves.forEach {
            assert(it.isAttack())
            assert(!it.isCastle())
            assert(it.getAttackedPiece() == attackedPiece)
            assert(it.getColor() == PieceColor.WHITE)
            assert(it.getTo() == end)
            assert(it.getDescription().contains("WHITE PAWN @ $start to $end."))
            assert(it.getDescription().contains("Promoted to "))
            assert(it.getDescription().contains("Kills BLACK PAWN."))
        }
    }

    @Test
    fun `get castle move`() {
        val king: Piece = mock()
        whenever(king.getColor()).thenReturn(PieceColor.WHITE)
        whenever(king.getPosition()).thenReturn(Pair(0, 4))
        whenever(king.getType()).thenReturn(GamePiece.KING)

        val castle: Piece = mock()
        whenever(castle.getPosition()).thenReturn(Pair(0, 0))
        whenever(castle.getColor()).thenReturn(PieceColor.WHITE)

        whenever(board.getPiece(Pair(0, 4))).thenReturn(king)

        val move = moveFactory.getMove(Pair(0, 4), castle, board)

        assert(move.isCastle())
        assert(!move.isPromotion())
        assert(!move.isAttack())
        assert(move.getAttackedPiece() == null)
        assert(move.getTo() == Pair(0, 2))
        assert(move.getDescription().contains("WHITE QUEEN_SIDE castle"))


    }

}