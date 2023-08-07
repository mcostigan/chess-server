package com.example.chessserver.gameplay

import com.example.chessserver.game.GameService
import com.example.chessserver.game.MoveResult
import com.example.chessserver.gameplay.move.ClientMove
import com.example.chessserver.gameplay.move.Move
import com.example.chessserver.gameplay.pieces.PieceColor
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameplayService(private val gameService: GameService) {

    fun makeMove(gameId: UUID, clientMove: ClientMove): MoveResult {
        val game = get(gameId)
        return game.move(clientMove)
    }

    fun getAvailableMoves(gameId: UUID): AvailableMoves {
        val game = get(gameId)
        return game.availableMoves()
    }

    fun getAvailableMoves(gameId: UUID, userId: UUID): AvailableMoves {
        val game = get(gameId)

        return game.availableMoves(userId)

    }

    private fun get(gameId: UUID) = gameService.get(gameId)
}

data class AvailableMoves(val userId: UUID, val moves: Collection<Move>)