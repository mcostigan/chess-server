package com.example.chessserver.game

import com.example.chessserver.gameplay.AvailableMoves
import com.example.chessserver.gameplay.board.BoardFactory
import com.example.chessserver.gameplay.board.IBoard
import com.example.chessserver.gameplay.check.GameStatusService
import com.example.chessserver.gameplay.move.ClientMove
import com.example.chessserver.gameplay.move.InvalidMoveException
import com.example.chessserver.gameplay.move.MoveConversionService
import com.example.chessserver.gameplay.pieces.PieceColor
import com.example.chessserver.user.IPlayer
import com.example.chessserver.waitingRoom.Experience
import java.util.*

interface IGame {
    val id: UUID
    val white: IPlayer
    val black: IPlayer?
    val minExperience: Experience
    val maxExperience: Experience
    fun addPlayer(user: IPlayer)

    @Throws(exceptionClasses = [UnsupportedOperationException::class, InvalidAuthorException::class, InvalidMoveException::class])
    fun move(clientMove: ClientMove): MoveResult
    fun matchesRequest(gameRequest: GameRequest): Boolean
    fun availableMoves(): AvailableMoves
    fun availableMoves(userId: UUID): AvailableMoves
}

class Game(
    override val white: IPlayer,
    override val minExperience: Experience,
    override val maxExperience: Experience,
    private val boardFactory: BoardFactory,
    private val moveConversionService: MoveConversionService,
    private val gameStatusService: GameStatusService
) :
    IGame {
    override val id: UUID = UUID.randomUUID()
    private var state: GameState = PendingGameState()
    private var board: IBoard? = null

    private var turn: PieceColor = PieceColor.WHITE

    override var black: IPlayer? = null
    override fun addPlayer(user: IPlayer) {
        this.state.addPlayer(user)
    }

    override fun matchesRequest(gameRequest: GameRequest): Boolean =
        gameRequest.minExperience <= this.maxExperience && gameRequest.maxExperience >= this.minExperience

    override fun availableMoves(): AvailableMoves {
        return AvailableMoves(if (turn == PieceColor.WHITE) white.id!! else black!!.id!!, board!!.availableMoves(turn))
    }

    override fun availableMoves(userId: UUID): AvailableMoves {
        if (userId != white.id && userId != black?.id) {
            throw Exception()
        }

        val moves = availableMoves()
        if (moves.userId == userId) {
            return moves
        }
        return AvailableMoves(userId, listOf())
    }


    override fun move(clientMove: ClientMove): MoveResult {
        val result = this.state.move(clientMove)

        if (result.gameStatus.isComplete) {
            this.state = CompleteGameState()
        }

        return result
    }

    private fun setState(state: GameState) {
        this.state = state
    }

    private interface GameState {
        fun addPlayer(player: IPlayer)
        fun move(clientMove: ClientMove): MoveResult
    }

    private inner class PendingGameState : GameState {
        override fun addPlayer(player: IPlayer) {
            black = player
            setState(LiveGameState())
            board = boardFactory.get()
        }

        override fun move(clientMove: ClientMove): MoveResult {
            throw UnsupportedOperationException()
        }
    }

    private inner class LiveGameState : GameState {
        override fun addPlayer(player: IPlayer) {
            throw UnsupportedOperationException()
        }

        // TODO return pair of move result and available moves
        override fun move(clientMove: ClientMove): MoveResult {
            validateAuthor(clientMove.userId)

            val move = moveConversionService.convertAndValidate(clientMove, turn, board!!)
            board!!.move(move)
            turn = turn.opposite()

            val inCheck = board!!.isCheck(turn)
            val availableMoves = board!!.availableMoves(turn)
            val status = gameStatusService.getGameState(inCheck, availableMoves.isNotEmpty())
            return MoveResult(move, status)
        }

        private fun validateAuthor(userId: UUID) {
            if (turn == PieceColor.WHITE) {
                if (userId != white.id) {
                    throw InvalidAuthorException(userId, white.id!!)
                }
            } else {
                if (userId != black?.id) {
                    throw InvalidAuthorException(userId, black?.id!!)
                }
            }
        }
    }

    private inner class CompleteGameState : GameState {
        override fun addPlayer(player: IPlayer) {
            throw UnsupportedOperationException()
        }

        override fun move(clientMove: ClientMove): MoveResult {
            throw UnsupportedOperationException()
        }

    }

}

class InvalidAuthorException(authorId: UUID, expected: UUID) :
    Exception("Invalid author: $authorId received, $expected expected")