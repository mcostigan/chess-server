package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import com.example.chessserver.gameplay.pieces.Promotable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(`as` = Move::class)
interface Move {
    fun getFrom(): Pair<Int, Int>
    fun getDescription(): String
    fun isAttack(): Boolean
    fun isCastle(): Boolean
    fun isPromotion(): Boolean
    fun getPromotionTarget(): GamePiece?
    fun getColor(): PieceColor

    @JsonIgnore
    fun getAttackedPiece(): Piece?
    fun getTo(): Pair<Int, Int>
}

class BasicMove(val piece: Piece, val endPosition: Pair<Int, Int>) : Move {
    private val description: String =
        "${piece.getColor()} ${piece.getType()} @ ${piece.getPosition()} to $endPosition."
    private val from = Pair(piece.getPosition().first, piece.getPosition().second)

    override fun getFrom(): Pair<Int, Int> = from
    override fun getDescription(): String = description
    override fun isAttack(): Boolean = false
    override fun getColor(): PieceColor = piece.getColor()
    override fun getAttackedPiece(): Piece? = null
    override fun getTo(): Pair<Int, Int> = endPosition
    override fun isCastle(): Boolean = false
    override fun isPromotion(): Boolean = false
    override fun getPromotionTarget(): GamePiece? = null
}

abstract class MoveDecorator(val wrapped: Move) : Move {
    override fun getColor(): PieceColor = wrapped.getColor()
    override fun isAttack(): Boolean = wrapped.isAttack()
    override fun isCastle(): Boolean = wrapped.isCastle()
    override fun getAttackedPiece(): Piece? = wrapped.getAttackedPiece()
    override fun getTo(): Pair<Int, Int> = wrapped.getTo()
    override fun isPromotion(): Boolean = false
    override fun getPromotionTarget(): GamePiece? = wrapped.getPromotionTarget()
    override fun getFrom(): Pair<Int, Int> = wrapped.getFrom()
}

class AttackDecorator(wrapped: Move, internal val attackedPiece: Piece) : MoveDecorator(wrapped) {
    private val description = "Kills ${attackedPiece.getColor()} ${attackedPiece.getType()}."
    override fun getDescription(): String {
        return wrapped.getDescription() + " $description"
    }

    override fun isAttack(): Boolean = true
    override fun getAttackedPiece(): Piece = attackedPiece

}

class PromotionDecorator(wrapped: Move, internal val toPromote: Promotable, internal val promoteTo: GamePiece) :
    MoveDecorator(wrapped) {
    private val description = "Promoted to ${promoteTo}."
    override fun getDescription(): String {
        return wrapped.getDescription() + " $description"
    }

    override fun isPromotion(): Boolean = true
    override fun getPromotionTarget() = promoteTo

}


enum class CastleSide {
    QUEEN_SIDE,
    KING_SIDE
}

class CastleDecorator(wrapped: Move, internal val castle: Piece) :
    MoveDecorator(wrapped) {

    override fun getDescription(): String = "${wrapped.getColor()} ${getSide()} castle."

    override fun isCastle(): Boolean = true
    override fun isAttack(): Boolean = false

    fun getSide(): CastleSide = if (castle.getPosition().second == 0) CastleSide.QUEEN_SIDE else CastleSide.KING_SIDE

}