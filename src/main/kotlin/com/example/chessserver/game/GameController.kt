package com.example.chessserver.game

import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/game")
@CrossOrigin
class GameController(val gameService: GameService, private val simpleMessagingTemplate: SimpMessageSendingOperations) {
    @PostMapping
    fun createGame(@RequestBody clientGameRequest: ClientGameRequest, authentication: Authentication): IGame {
        val forUser: UUID = UUID.fromString(authentication.name)
        val game = gameService.create(forUser, clientGameRequest.minExperience, clientGameRequest.maxExperience)

        game.black?.let {
            simpleMessagingTemplate.convertAndSend("/topic/game/${game.id}/players", it)
            simpleMessagingTemplate.convertAndSendToUser(game.white.id!!.toString(), "/topic/game/${game.id}/moves", game.availableMoves().moves)
        }
        return game
    }

}