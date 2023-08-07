package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.patterns.MovementPatternFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
internal class PieceFactoryTest {

    @Mock
    lateinit var movementPatternFactory: MovementPatternFactory

    @InjectMocks
    lateinit var pieceFactory: PieceFactory

    val position = Pair(0, 0)
    val color = PieceColor.WHITE

    @Test
    fun buildPawn() {
        val pawn = pieceFactory.buildPawn(position, color)
        assert(pawn.getColor() == color)
        assert(pawn.getPosition() == position)
        assert(pawn.getType() == GamePiece.PAWN)
        verify(movementPatternFactory).getAttackDiagonalMovementPattern(1)
    }

    @Test
    fun buildRook() {
        val pawn = pieceFactory.buildRook(position, color)
        assert(pawn.getColor() == color)
        assert(pawn.getPosition() == position)
        assert(pawn.getType() == GamePiece.ROOK)
        verify(movementPatternFactory).getRookMovementPatterns()
    }

    @Test
    fun buildKnight() {
        val pawn = pieceFactory.buildKnight(position, color)
        assert(pawn.getColor() == color)
        assert(pawn.getPosition() == position)
        assert(pawn.getType() == GamePiece.KNIGHT)
        verify(movementPatternFactory).getKnightMovementPatterns()
    }

    @Test
    fun buildBishop() {
        val pawn = pieceFactory.buildBishop(position, color)
        assert(pawn.getColor() == color)
        assert(pawn.getPosition() == position)
        assert(pawn.getType() == GamePiece.BISHOP)
        verify(movementPatternFactory).getBishopMovementPatterns()
    }

    @Test
    fun buildKing() {
        val pawn = pieceFactory.buildKing(position, color, mock(), mock())
        assert(pawn.getColor() == color)
        assert(pawn.getPosition() == position)
        assert(pawn.getType() == GamePiece.KING)
        verify(movementPatternFactory).getKingMovementPatterns()
    }

    @Test
    fun buildQueen() {
        val pawn = pieceFactory.buildQueen(position, color)
        assert(pawn.getColor() == color)
        assert(pawn.getPosition() == position)
        assert(pawn.getType() == GamePiece.QUEEN)
        verify(movementPatternFactory).getQueenMovementPatterns()
    }



}