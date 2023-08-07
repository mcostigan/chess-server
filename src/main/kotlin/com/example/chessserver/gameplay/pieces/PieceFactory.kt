package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.patterns.MovementPatternFactory
import org.springframework.stereotype.Component


@Component
class PieceFactory(private val movementPatternFactory: MovementPatternFactory) {

    fun buildPawn(position: Pair<Int, Int>, color: PieceColor): Piece =
        Pawn(position, color, this, movementPatternFactory)

    fun buildRook(position: Pair<Int, Int>, color: PieceColor): Piece = build(position, color, Rook::class.java)
    fun buildKnight(position: Pair<Int, Int>, color: PieceColor): Piece = build(position, color, Knight::class.java)
    fun buildBishop(position: Pair<Int, Int>, color: PieceColor): Piece = build(position, color, Bishop::class.java)
    fun buildKing(
        position: Pair<Int, Int>,
        color: PieceColor,
        kingSideCastle: Piece,
        queenSideCastle: Piece
    ): Piece = King(position, color, kingSideCastle, queenSideCastle, movementPatternFactory)

    fun buildQueen(position: Pair<Int, Int>, color: PieceColor): Piece = build(position, color, Queen::class.java)

    private fun <T : Piece> build(position: Pair<Int, Int>, color: PieceColor, type: Class<T>): T =
        type.getConstructor(Pair::class.java, PieceColor::class.java, MovementPatternFactory::class.java)
            .newInstance(position, color, this.movementPatternFactory)

    fun get(type: GamePiece, position: Pair<Int, Int>, color: PieceColor): Piece = when (type) {
        GamePiece.PAWN -> buildPawn(position, color)
        GamePiece.QUEEN -> buildQueen(position, color)
        GamePiece.KING -> throw UnsupportedOperationException("Cannot build King from type")
        GamePiece.BISHOP -> buildBishop(position, color)
        GamePiece.KNIGHT -> buildKnight(position, color)
        GamePiece.ROOK -> buildRook(position, color)
    }


}