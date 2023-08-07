package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.Promotable
import org.springframework.stereotype.Component

@Component
class MoveFactory {
    fun getMove(startPosition: Pair<Int, Int>, endPosition: Pair<Int, Int>, board: IBoard): List<Move> {
        val piece = board.getPiece(startPosition) ?: throw NoSuchElementException()
        val occupyingPiece = board.getPiece(endPosition)
        if (occupyingPiece?.sameTeam(piece) == true) {
            return listOf()
        }

        var move: Move = BasicMove(piece, endPosition)
        // if an enemy piece occupies the spot, then attack
        if (occupyingPiece != null) {
            move = AttackDecorator(move, occupyingPiece)
        }

        // if the promotable piece has reached the end of the board, promote
        if (piece is Promotable && piece.canPromote() && (endPosition.first % 7 == 0)) {
            return Piece.getPromotionTargets().map { PromotionDecorator(move, piece, it) }
        }
        return listOf(move)

    }

    fun getMove(startPosition: Pair<Int, Int>, castle: Piece, board: IBoard): Move {
        val king = board.getPiece(startPosition) ?: throw NoSuchElementException()
        if (king.getType() != GamePiece.KING) {
            throw IllegalArgumentException()
        }
        val isLeftRook = startPosition.second > castle.getPosition().second
        val kingPosition = Pair(
            startPosition.first,
            startPosition.second +
                    if (isLeftRook) -2 else 2
        )
        val move: Move = BasicMove(king, kingPosition)
        return CastleDecorator(move, castle)
    }
}