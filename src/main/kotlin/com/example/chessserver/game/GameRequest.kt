package com.example.chessserver.game

import com.example.chessserver.user.IPlayer
import com.example.chessserver.waitingRoom.Experience

class GameRequest(
    val user: IPlayer,
    val minExperience: Experience = Experience.NOVICE,
    val maxExperience: Experience = Experience.EXPERT,
)

data class ClientGameRequest(
    val minExperience: Experience,
    val maxExperience: Experience
)