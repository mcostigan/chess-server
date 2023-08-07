package com.example.chessserver.gameplay.patterns

import com.example.chessserver.gameplay.move.MoveFactory
import com.example.chessserver.gameplay.pieces.Piece
import org.springframework.stereotype.Component

@Component
class MovementPatternFactory(
    private val moveFactory: MoveFactory
) {

    fun getQueenMovementPatterns(): Collection<MovementPattern> = listOf(
        getDiagonalMovementPattern(),
        getVerticalMovementPattern(),
        getHorizontalMovementPattern()
    )

    fun getRookMovementPatterns() = listOf(
        getHorizontalMovementPattern(),
        getVerticalMovementPattern()
    )

    fun getKnightMovementPatterns() = listOf(getLShapeMovementPattern())
    fun getBishopMovementPatterns() = listOf(getDiagonalMovementPattern())
    fun getKingMovementPatterns() =
        listOf(getVerticalMovementPattern(1), getHorizontalMovementPattern(1), getDiagonalMovementPattern(1))

    private fun getVerticalMovementPattern(range: Int = Int.MAX_VALUE): MovementPattern =
        VerticalMovementPattern(range, moveFactory)

    private fun getHorizontalMovementPattern(range: Int = Int.MAX_VALUE): MovementPattern =
        HorizontalMovementPattern(range, moveFactory)

    private fun getDiagonalMovementPattern(range: Int = Int.MAX_VALUE): MovementPattern =
        DiagonalMovementPattern(range, moveFactory)

    fun getForwardMovementPattern(range: Int, canAttack: Boolean = true): MovementPattern =
        ForwardMovementPattern(range, moveFactory, canAttack)

    fun getAttackDiagonalMovementPattern(range: Int): MovementPattern =
        AttackDiagonalMovementPattern(range, moveFactory)

    private fun getLShapeMovementPattern(): MovementPattern = LShapeMovementPattern(moveFactory)
    fun getCastleMovementPattern(kingSideRook: Piece, queenSideRook: Piece): MovementPattern =
        CastleMovementPattern(kingSideRook, queenSideRook, moveFactory)

}