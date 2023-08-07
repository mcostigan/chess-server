package com.example.chessserver.gameplay.move

import com.example.chessserver.gameplay.pieces.GamePiece
import com.example.chessserver.gameplay.pieces.Piece
import com.example.chessserver.gameplay.pieces.PieceColor

abstract class InvalidMoveException(reason: String) : Exception(reason)
class InvalidPromotionTarget(gamePiece: GamePiece) : InvalidMoveException("Promotion to $gamePiece is not allowed. Allowed promotions are: ${Piece.getPromotionTargets()}")
class NullPieceException(location: Pair<Int, Int>) : InvalidMoveException("No piece @ $location")
class WrongColorException(expected: PieceColor, got: PieceColor) :
    InvalidMoveException("Piece is $got. Expected $expected")

class UnavailableMoveException : InvalidMoveException("Move is not available to piece")