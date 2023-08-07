package com.example.chessserver.game

import com.example.chessserver.gameplay.check.GameStatus
import com.example.chessserver.gameplay.move.Move

class MoveResult(val move: Move, val gameStatus: GameStatus)