package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.patterns.MovementPattern
import com.example.chessserver.gameplay.patterns.MovementPatternFactory

internal class King(
    position: Pair<Int, Int>,
    color: PieceColor,
    private val kingSideRook: Piece,
    private val queenSideRook: Piece,
    private val movementPatternFactory: MovementPatternFactory
) : Piece(
    position,
    color, movementPatternFactory.getKingMovementPatterns()
) {
    private var kingState: KingState = UnmovedKingState(this)

    override fun move(position: Pair<Int, Int>) {
        super.move(position)
        kingState.move()

    }

    override fun getType(): GamePiece = GamePiece.KING

    override fun getMovementPatterns(): Collection<MovementPattern> {
        val result = super.getMovementPatterns().toMutableList()
        result.addAll(kingState.getMovementPatterns())
        return result
    }

    private fun setState(kingState: KingState) {
        this.kingState = kingState
    }


    private abstract inner class KingState(
        protected var context: King,
        protected val movementPatternFactory: MovementPatternFactory
    ) {
        abstract fun getMovementPatterns(): Collection<MovementPattern>
        abstract fun move()
    }


    private inner class MovedKingState(king: King) : KingState(king, this.movementPatternFactory) {

        override fun getMovementPatterns(): Collection<MovementPattern> = listOf()

        override fun move() {}

    }

    private inner class UnmovedKingState(king: King) : KingState(king, this.movementPatternFactory) {

        override fun getMovementPatterns(): Collection<MovementPattern> =
            listOf(this.movementPatternFactory.getCastleMovementPattern(kingSideRook, queenSideRook))

        override fun move() {
            context.setState(MovedKingState(context))
        }

    }
}
