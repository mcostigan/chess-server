package com.example.chessserver.gameplay.check

import org.springframework.stereotype.Service

@Service
class GameStatusService {


    /**
     * Return one of four possible game states: `NORMAL`, `CHECK`, `CHECKMATE, `STALEMATE` (rarer outcomes are ignored).
     *
     * https://en.wikipedia.org/wiki/Chess#End_of_the_game
     *
     */
    fun getGameState(inCheck: Boolean, hasMoves: Boolean): GameStatus {
        return when (true) {
            !inCheck && !hasMoves -> GameStatus.STALEMATE
            inCheck && !hasMoves -> GameStatus.CHECKMATE
            inCheck && hasMoves -> GameStatus.CHECK
            else -> GameStatus.NORMAL
        }

    }
}

enum class GameStatus(val isComplete: Boolean) {
    NORMAL(false),
    CHECK(false),
    CHECKMATE(true),
    STALEMATE(true)
}