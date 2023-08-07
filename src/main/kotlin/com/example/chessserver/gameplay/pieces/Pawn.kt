package com.example.chessserver.gameplay.pieces

import com.example.chessserver.gameplay.patterns.MovementPattern
import com.example.chessserver.gameplay.patterns.MovementPatternFactory

internal class Pawn(
    position: Pair<Int, Int>,
    color: PieceColor,
    private val pieceFactory: PieceFactory,
    private val movementPatternFactory: MovementPatternFactory
) :
    Piece(
        position,
        color,
        listOf(
            movementPatternFactory.getAttackDiagonalMovementPattern(1)
        )
    ), Promotable {
    private var pawnState: PawnState = UnmovedPawnState()

    override fun getMovementPatterns(): Collection<MovementPattern> {
        val result = super.getMovementPatterns().toMutableList()
        result.addAll(pawnState.getMovementPatterns())
        return result
    }


    override fun move(position: Pair<Int, Int>) {
        super.move(position)
        this.pawnState.move(position)
    }

    override fun promote(type: GamePiece) {
        this.pawnState.promote(type)
    }

    override fun canPromote(): Boolean = this.pawnState.canPromote()


    private fun setState(state: PawnState) {
        this.pawnState = state
    }

    override fun getType(): GamePiece = this.pawnState.getType()


    private abstract inner class PawnState {
        abstract fun getMovementPatterns(): Collection<MovementPattern>
        abstract fun move(position: Pair<Int, Int>)
        abstract fun getType(): GamePiece
        abstract fun promote(type: GamePiece)
        abstract fun canPromote(): Boolean
    }

    private inner class UnmovedPawnState :
        PawnState() {
        override fun getMovementPatterns(): Collection<MovementPattern> =
            listOf(movementPatternFactory.getForwardMovementPattern(2, false))

        override fun move(position: Pair<Int, Int>) {
            setState(MovedPawnState())
        }

        override fun getType(): GamePiece = GamePiece.PAWN
        override fun promote(type: GamePiece) {}
        override fun canPromote(): Boolean = false
    }

    private inner class MovedPawnState :
        PawnState() {
        override fun getMovementPatterns(): Collection<MovementPattern> =
            listOf(movementPatternFactory.getForwardMovementPattern(1, false))

        override fun move(position: Pair<Int, Int>) {}
        override fun getType(): GamePiece = GamePiece.PAWN

        override fun promote(type: GamePiece) {
            // temporarily create a piece of type `type`, then extract its movement patterns and apply to new pawn state
            val tmp = pieceFactory.get(type, getPosition(), getColor())
            setState(PromotedPawnState(tmp.getMovementPatterns().toList(), type))
        }

        override fun canPromote() = true

    }

    private inner class PromotedPawnState(
        private val movementPatterns: Collection<MovementPattern>,
        private val type: GamePiece,
    ) :
        PawnState() {
        override fun getMovementPatterns(): Collection<MovementPattern> = this.movementPatterns
        override fun move(position: Pair<Int, Int>) {}
        override fun getType(): GamePiece = this.type
        override fun promote(type: GamePiece) {}
        override fun canPromote(): Boolean = false
    }
}


