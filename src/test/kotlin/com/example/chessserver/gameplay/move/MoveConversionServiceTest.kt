package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class MoveConversionServiceTest {

    @InjectMocks
    private lateinit var converter : MoveConversionService

    private val board: IBoard = mock()
    private val turn = PieceColor.WHITE
    private var clientMove = ClientMove(Pair(0, 0), Pair(1, 0))

    @Test
    fun `should throw error if no piece at square`() {
        whenever(board.getPiece(Pair(0, 0))).thenReturn(null)
        assertThrows<NullPieceException> {
            converter.convertAndValidate(clientMove, turn, board)
        }
    }

    @Test
    fun `should throw error if piece color does not match turn`() {
        val piece: Piece = mock()
        whenever(piece.getColor()).thenReturn(PieceColor.BLACK)
        whenever(board.getPiece(Pair(0, 0))).thenReturn(piece)
        assertThrows<WrongColorException> {
            converter.convertAndValidate(clientMove, turn, board)
        }
    }

    @Test
    fun `should throw error if move does not match available moves`() {
        val mockMove: Move = mock()
        whenever(mockMove.getFrom()).thenReturn(Pair(0, 0))
        whenever(mockMove.getTo()).thenReturn(Pair(2, 0))

        val piece: Piece = mock()
        whenever(piece.getColor()).thenReturn(PieceColor.WHITE)

        whenever(board.availableMoves(PieceColor.WHITE)).thenReturn(listOf(mockMove))
        whenever(board.getPiece(Pair(0, 0))).thenReturn(piece)
        assertThrows<UnavailableMoveException> {
            converter.convertAndValidate(clientMove, turn, board)
        }
    }

    @Test
    fun `should throw error if move is promotion and promotion target is not supported`() {
        clientMove = ClientMove(Pair(6, 0), Pair(7, 0), GamePiece.KING)
        val mockMove: Move = mock()
        whenever(mockMove.getFrom()).thenReturn(Pair(6, 0))
        whenever(mockMove.getTo()).thenReturn(Pair(7, 0))
        whenever(mockMove.isPromotion()).thenReturn(true)
        whenever(mockMove.getPromotionTarget()).thenReturn(GamePiece.QUEEN)

        val piece: Piece = mock()
        whenever(piece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(piece.getType()).thenReturn(GamePiece.PAWN)

        whenever(board.availableMoves(turn)).thenReturn(listOf(mockMove))
        whenever(board.getPiece(Pair(6, 0))).thenReturn(piece)
        assertThrows<InvalidPromotionTarget> {
            converter.convertAndValidate(clientMove, turn, board)
        }
    }

    @Test
    fun `should return promotion move if is valid`() {
        clientMove = ClientMove(Pair(6, 0), Pair(7, 0), GamePiece.QUEEN)
        val mockMove: Move = mock()
        whenever(mockMove.getFrom()).thenReturn(Pair(6, 0))
        whenever(mockMove.getTo()).thenReturn(Pair(7, 0))
        whenever(mockMove.isPromotion()).thenReturn(true)
        whenever(mockMove.getPromotionTarget()).thenReturn(GamePiece.QUEEN)

        val piece: Piece = mock()
        whenever(piece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(piece.getType()).thenReturn(GamePiece.PAWN)

        whenever(board.availableMoves(turn)).thenReturn(listOf(mockMove))
        whenever(board.getPiece(Pair(6, 0))).thenReturn(piece)
        val result = converter.convertAndValidate(clientMove, turn, board)

        assert(result.isPromotion())
        assert(result.getPromotionTarget() == GamePiece.QUEEN)
    }

    @Test
    fun `should return move if is valid`() {
        val mockMove: Move = mock()
        whenever(mockMove.getFrom()).thenReturn(Pair(0, 0))
        whenever(mockMove.getTo()).thenReturn(Pair(1, 0))

        val piece: Piece = mock()
        whenever(piece.getColor()).thenReturn(PieceColor.WHITE)
        whenever(piece.getType()).thenReturn(GamePiece.PAWN)

        whenever(board.availableMoves(turn)).thenReturn(listOf(mockMove))
        whenever(board.getPiece(Pair(0, 0))).thenReturn(piece)
        val result = converter.convertAndValidate(clientMove, turn, board)

        assert(result.getTo() == Pair(1, 0))
    }
}