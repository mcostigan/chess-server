package com.example.chessserver.game

import com.example.chessserver.user.PlayerService
import com.example.chessserver.waitingRoom.Experience
import com.example.chessserver.waitingRoom.GameMatch
import com.example.chessserver.waitingRoom.SimpleWaitingRoom
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameService(
    private val playerService: PlayerService,
    private val waitingRoom: SimpleWaitingRoom,
    private val gameRepo: GameRepo
) {

    init {
        this.waitingRoom.subscribe { data: GameMatch -> gameRepo.addGame(data.game) }
    }

    fun create(forUser: UUID, minExperience: Experience, maxExperience: Experience): IGame {
        val user = playerService.getPlayer(forUser)
        val gameRequest = GameRequest(user, minExperience, maxExperience)
        return waitingRoom.matchOrAdd(gameRequest)
    }

    fun get(gameId: UUID): IGame = gameRepo.getGame(gameId)
}