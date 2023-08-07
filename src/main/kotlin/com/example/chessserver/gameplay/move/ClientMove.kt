package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.pieces.GamePiece
import java.util.*

data class ClientMove(val from: Pair<Int, Int>, val to: Pair<Int, Int>, val promotionTarget: GamePiece? = null) {
    lateinit var userId: UUID
}