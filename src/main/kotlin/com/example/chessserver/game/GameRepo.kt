package com.example.chessserver.game

import org.springframework.stereotype.Component
import java.util.*

interface GameRepo {
    fun addGame(game: IGame)
    fun getGame(id: UUID): IGame
}


@Component
class InMemoryGameRepo : GameRepo {
    // TODO: thread safe
    private var data: MutableMap<UUID, IGame> = mutableMapOf()
    override fun addGame(game: IGame) {
        data[game.id] = game
    }

    override fun getGame(id: UUID): IGame {
        return data[id] ?: throw NoSuchElementException()
    }


}