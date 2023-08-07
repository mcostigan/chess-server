package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.patterns.MovementPatternFactory


internal class Bishop(position: Pair<Int, Int>, color: PieceColor, movementPatternFactory: MovementPatternFactory) :
    Piece(
        position, color, movementPatternFactory.getBishopMovementPatterns()
    ) {
    override fun getType(): GamePiece = GamePiece.BISHOP
}

internal class Knight(position: Pair<Int, Int>, color: PieceColor, movementPatternFactory: MovementPatternFactory) :
    Piece(position, color, movementPatternFactory.getKnightMovementPatterns()) {
    override fun getType(): GamePiece = GamePiece.KNIGHT
}

internal class Queen(position: Pair<Int, Int>, color: PieceColor, movementPatternFactory: MovementPatternFactory) :
    Piece(
        position,
        color,
        movementPatternFactory.getQueenMovementPatterns()
    ) {
    override fun getType(): GamePiece = GamePiece.QUEEN
}

internal class Rook(position: Pair<Int, Int>, color: PieceColor, movementPatternFactory: MovementPatternFactory) :
    Piece(
        position,
        color,
        movementPatternFactory.getRookMovementPatterns()
    ) {
    private val castlePosition: Pair<Int, Int> =
        Pair(this.getPosition().first, if (this.getPosition().second == 0) 3 else 5)

    override fun getType(): GamePiece = GamePiece.ROOK
    fun castle() = castlePosition
}