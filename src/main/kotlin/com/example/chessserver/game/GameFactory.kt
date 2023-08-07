package com.example.chessserver.game

import com.example.chessserver.gameplay.board.BoardFactory
import com.example.chessserver.gameplay.check.GameStatusService
import com.example.chessserver.gameplay.move.MoveConversionService
import com.example.chessserver.user.IPlayer
import com.example.chessserver.waitingRoom.Experience
import org.springframework.stereotype.Component

@Component
class GameFactory(
    private val boardFactory: BoardFactory,
    private val moveConversionService: MoveConversionService,
    private val gameStatusService: GameStatusService
) {

    fun get(
        whitePlayer: IPlayer,
        minExperience: Experience = Experience.NOVICE,
        maxExperience: Experience = Experience.EXPERT
    ): IGame {
        return Game(whitePlayer, minExperience, maxExperience, boardFactory, moveConversionService, gameStatusService)
    }
}