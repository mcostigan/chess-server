package com.example.chessserver.game

import com.example.chessserver.gameplay.board.BoardFactory
import com.example.chessserver.gameplay.check.GameStatusService
import com.example.chessserver.gameplay.move.MoveConversionService
import com.example.chessserver.user.IPlayer
import com.example.chessserver.waitingRoom.Experience
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.util.*

internal class GameTest {

    private data class Player(override val name: String) : IPlayer {
        override val id: UUID = UUID.randomUUID()
        override val experience: Experience = Experience.INTERMEDIATE
    }

    private val boardFactory: BoardFactory = mock()
    private val moveConversionService: MoveConversionService = mock()
    private val gameStatusService: GameStatusService = mock()
    private val request1 = GameRequest(Player("1"), Experience.NOVICE, Experience.INTERMEDIATE)
    private val game =
        Game(request1.user, request1.minExperience, request1.maxExperience, boardFactory, moveConversionService, gameStatusService)
    private val request2 = GameRequest(Player("2"), Experience.ADVANCED, Experience.EXPERT)
    private val request3 = GameRequest(Player("3"), Experience.BEGINNER, Experience.ADVANCED)

    @Test
    fun `returns true when requests overlap`() {
        assert(game.matchesRequest(request3))
    }

    @Test
    fun `returns false when requests overlap`() {
        assert(!game.matchesRequest(request2))
    }
}