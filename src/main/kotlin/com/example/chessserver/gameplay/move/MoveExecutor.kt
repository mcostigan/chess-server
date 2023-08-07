package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.Rook
import org.springframework.stereotype.Component


interface MoveExecutor {
    /**
     * Update the state of the squares and piece objects to reflect a given move
     */
    fun executeMove(move: Move, squares: MutableList<MutableList<Piece?>>)
}

@Component
class DefaultMoveExecutor : MoveExecutor {

    override fun executeMove(move: Move, squares: MutableList<MutableList<Piece?>>) {
        when (true) {
            move is MoveDecorator -> {
                executeMove(move.wrapped, squares)
                when (true) {
                    move is AttackDecorator -> executeAttack(move, squares)
                    move is PromotionDecorator -> executePromotion(move, squares)
                    move is CastleDecorator -> executeCastle(move, squares)
                    else -> {}
                }
            }
            else -> executeBasicMove(move as BasicMove, squares)
        }
    }

    protected fun executeAttack(move: AttackDecorator, squares: MutableList<MutableList<Piece?>>) {
        val attackedPiece = move.attackedPiece
        val oldPosition = attackedPiece.getPosition()

        move.attackedPiece.kill()
        if (squares[oldPosition.first][oldPosition.second] == attackedPiece) {
            squares[oldPosition.first][oldPosition.second] = null
        }

    }

    protected fun executePromotion(move: PromotionDecorator, squares: MutableList<MutableList<Piece?>>) {
        move.toPromote.promote(move.promoteTo)
    }

    protected fun executeCastle(move: CastleDecorator, squares: MutableList<MutableList<Piece?>>) {
        val rook = move.castle as Rook
        setPiece(move.castle, rook.castle(), squares)
    }

    protected fun executeBasicMove(move: BasicMove, squares: MutableList<MutableList<Piece?>>) {
        setPiece(move.piece, move.endPosition, squares)
    }

    protected fun setPiece(piece: Piece, position: Pair<Int, Int>, squares: MutableList<MutableList<Piece?>>) {
        val oldPosition = piece.getPosition()
        piece.move(position)
        squares[oldPosition.first][oldPosition.second] = null
        squares[position.first][position.second] = piece
    }
}

class HypotheticalMoveExecutor : DefaultMoveExecutor() {

    override fun executeAttack(move: AttackDecorator, squares: MutableList<MutableList<Piece?>>) {
        val attackedPiece = move.attackedPiece
        val oldPosition = attackedPiece.getPosition()

        if (squares[oldPosition.first][oldPosition.second] == attackedPiece) {
            squares[oldPosition.first][oldPosition.second] = null
        }
    }

    override fun executePromotion(move: PromotionDecorator, squares: MutableList<MutableList<Piece?>>) {}

    override fun setPiece(piece: Piece, position: Pair<Int, Int>, squares: MutableList<MutableList<Piece?>>) {
        val oldPosition = piece.getPosition()
        squares[oldPosition.first][oldPosition.second] = null
        squares[position.first][position.second] = piece
    }
}