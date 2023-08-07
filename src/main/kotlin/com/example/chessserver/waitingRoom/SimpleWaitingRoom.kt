package com.example.chessserver.waitingRoom

import com.example.chessserver.game.GameFactory
import com.example.chessserver.game.GameRequest
import com.example.chessserver.game.IGame
import org.springframework.stereotype.Component
import java.util.*

@Component
class SimpleWaitingRoom(private val gameFactory: GameFactory) : WaitingRoom() {

    // TODO make thread safe
    private var pendingGames: MutableCollection<IGame> = mutableListOf()
    private var players: MutableSet<UUID> = mutableSetOf()

    override fun matchOrAdd(request: GameRequest): IGame {
        if (players.contains(request.user.id)) {
            throw DuplicateGameRequestException()
        }
        val game = pendingGames.firstOrNull { it.matchesRequest(request) }
        return if (game != null) {
            game.addPlayer(request.user)
            pendingGames.remove(game)
            players.remove(game.white.id)
            this.notify(GameMatch(game))
            game
        } else {
            val newGame = this.gameFactory.get(request.user, request.minExperience, request.maxExperience)
            pendingGames.add(newGame)
            players.add(request.user.id!!)
            newGame
        }
    }

    fun getSize(): Int = this.pendingGames.size

}