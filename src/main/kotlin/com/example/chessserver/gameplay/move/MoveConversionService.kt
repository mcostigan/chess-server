package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.check.CheckDetectionService
import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.springframework.stereotype.Service


@Service
class MoveConversionService {

    fun convertAndValidate(clientMove: ClientMove, turn: PieceColor, board: IBoard): Move {
        // client move should not be referencing a null square
        val from = board.getPiece(clientMove.from) ?: throw NullPieceException(clientMove.from)

        // should be moving piece of own color
        if (from.getColor() != turn) {
            throw WrongColorException(from.getColor(), turn)
        }

        // move should match an available move
        val candidateMoves = board.availableMoves(turn)
            .filter {it.getFrom()==clientMove.from && it.getTo() == clientMove.to }

        if (isPromotion(from, clientMove.to)) {
            return (clientMove.promotionTarget ?: GamePiece.QUEEN).let { t ->
                candidateMoves.filter { it.isPromotion() }
                    .firstOrNull { it.getPromotionTarget() == t } ?: throw InvalidPromotionTarget(t) }
        }

        return candidateMoves.firstOrNull()
            ?: throw UnavailableMoveException()
    }

    /**
     * Detects if the move is a pawn reaching the end of the board, denoting promotion
     */
    private fun isPromotion(piece: Piece, to: Pair<Int, Int>) = piece.getType() == GamePiece.PAWN && (to.first % 7 == 0)
}

