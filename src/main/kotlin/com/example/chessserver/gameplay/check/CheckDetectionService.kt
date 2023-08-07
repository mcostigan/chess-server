package com.example.chessserver.gameplay.check

import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.PieceColor
import org.springframework.stereotype.Service

@Service
class CheckDetectionService {

    /**
     * Given a `board`, returns `true` if `team` is in check. Else, `false`
     *
     * A team is in check if any enemies pieces can attack the team's King.
     */
    fun isCheck(team: PieceColor, board: IBoard): Boolean {
        return board.getPiecesByColor(team.opposite())
            .flatMap { it.getAvailableMoves(board) }
            .any { it.isAttack() && it.getAttackedPiece()?.getType() == GamePiece.KING }
    }

    /**
     * Returns `true` if a piece on `team` can attack a piece in the `target` position. Else, `false`
     *
     */
    fun canAttack(team: PieceColor, target: Pair<Int, Int>, board: IBoard): Boolean {
        return board.getPiecesByColor(team).flatMap { it.getAvailableMoves(board) }
            .any { it.isAttack() && it.getAttackedPiece()?.getPosition() == target }
    }

}

