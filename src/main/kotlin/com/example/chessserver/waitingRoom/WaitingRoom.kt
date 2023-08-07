package com.example.chessserver.waitingRoom

import com.example.chessserver.game.GameRequest
import com.example.chessserver.game.IGame
import com.example.chessserver.observer.Observable

abstract class WaitingRoom : Observable<GameMatch>() {

    abstract fun matchOrAdd(request: GameRequest): IGame
}

enum class Experience {
    NOVICE,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

