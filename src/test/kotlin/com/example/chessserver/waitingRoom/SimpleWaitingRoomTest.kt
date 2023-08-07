package com.example.chessserver.waitingRoom

import com.example.chessserver.game.GameFactory
import com.example.chessserver.game.GameRequest
import com.example.chessserver.game.IGame
import com.example.chessserver.user.IPlayer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class SimpleWaitingRoomTest {
    @Mock
    lateinit var gameFactory: GameFactory

    @InjectMocks
    lateinit var waitingRoom: SimpleWaitingRoom


    private data class Player(override val id: UUID, override val name: String, override val experience: Experience) :
        IPlayer

    private val noviceRequest =
        GameRequest(Player(UUID.randomUUID(), "user1", Experience.NOVICE), Experience.NOVICE, Experience.NOVICE)
    private val intrmdtRequest = GameRequest(
        Player(UUID.randomUUID(), "user2", Experience.INTERMEDIATE),
        Experience.INTERMEDIATE,
        Experience.INTERMEDIATE
    )
    private val noviceBeginnerRequest = GameRequest(
        Player(UUID.randomUUID(), "user2", Experience.INTERMEDIATE),
        Experience.NOVICE,
        Experience.BEGINNER
    )

    @BeforeEach
    fun mockMethods() {
    }

    @Test
    fun `should add new games to waiting room`() {

        val firstGame: IGame = mock()
        val secondGame: IGame = mock()
        whenever(
            gameFactory.get(
                noviceRequest.user,
                noviceRequest.minExperience,
                noviceRequest.maxExperience
            )
        ).thenReturn(firstGame)
        whenever(
            gameFactory.get(
                intrmdtRequest.user,
                intrmdtRequest.minExperience,
                intrmdtRequest.maxExperience
            )
        ).thenReturn(secondGame)

        whenever(firstGame.matchesRequest(intrmdtRequest)).thenReturn(false)

        waitingRoom.matchOrAdd(noviceRequest)
        waitingRoom.matchOrAdd(intrmdtRequest)

        assert(waitingRoom.getSize() == 2)
        verify(gameFactory).get(noviceRequest.user, noviceRequest.minExperience, noviceRequest.maxExperience)
    }

    @Test
    fun `should match intersecting requests to same game `() {
        val firstGame: IGame = mock()

        whenever(firstGame.matchesRequest(noviceBeginnerRequest)).thenReturn(true)
        whenever(firstGame.white).thenReturn(noviceRequest.user)
        whenever(
            gameFactory.get(
                noviceRequest.user,
                noviceRequest.minExperience,
                noviceRequest.maxExperience
            )
        ).thenReturn(firstGame)

        waitingRoom.matchOrAdd(noviceRequest)
        waitingRoom.matchOrAdd(noviceBeginnerRequest)

        assert(waitingRoom.getSize() == 0)
        verify(gameFactory).get(any(), any(), any())
        verify(firstGame).addPlayer(any())

    }

    @Test
    fun `should throw exception if user already has a pending game`() {
        val game: IGame = mock()
        whenever(
            gameFactory.get(
                noviceRequest.user,
                noviceRequest.minExperience,
                noviceRequest.maxExperience
            )
        ).thenReturn(game)

        assertThrows<DuplicateGameRequestException> {
            waitingRoom.matchOrAdd(noviceRequest)
            waitingRoom.matchOrAdd(noviceRequest)
        }
    }
}