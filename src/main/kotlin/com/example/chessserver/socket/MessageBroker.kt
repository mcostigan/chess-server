package com.example.chessserver.socket

import com.example.chessserver.game.MoveResult
import com.example.chessserver.gameplay.GameplayService
import com.example.chessserver.gameplay.move.ClientMove
import com.example.chessserver.gameplay.move.Move
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.security.Principal
import java.util.*

@Controller
class MessageBroker(
    private val gameplayService: GameplayService,
    private val simpleMessagingTemplate: SimpMessageSendingOperations
) {


    @SubscribeMapping("/topic/game/{gameId}/moves")
    fun subscribeToMoves(@DestinationVariable gameId: UUID, principal: Principal): Collection<Move> {
        return gameplayService.getAvailableMoves(gameId, UUID.fromString(principal.name)).moves
    }

    @MessageMapping("/game/{gameId}/move")
    @SendTo("/topic/game/{gameId}/move")
    fun move(@DestinationVariable gameId: UUID, clientMove: ClientMove, principal: Principal): MoveResult {
        clientMove.userId = UUID.fromString(principal.name)

        val result = gameplayService.makeMove(gameId, clientMove)
        val moves = gameplayService.getAvailableMoves(gameId)
        simpleMessagingTemplate.convertAndSendToUser(
            moves.userId.toString(),
            "/topic/game/$gameId/moves",
            moves.moves
        )
        return result
    }
}

