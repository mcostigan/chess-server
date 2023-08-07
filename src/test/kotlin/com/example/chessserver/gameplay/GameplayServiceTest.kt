package com.example.chessserver.gameplay

import com.example.chessserver.game.GameService
import com.example.chessserver.game.IGame
import com.example.chessserver.game.InvalidAuthorException
import com.example.chessserver.game.MoveResult
import com.example.chessserver.gameplay.move.ClientMove
import com.example.chessserver.gameplay.move.InvalidMoveException
import com.example.chessserver.gameplay.move.NullPieceException
import com.example.chessserver.gameplay.move.UnavailableMoveException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class GameplayServiceTest {

    @Mock
    lateinit var gameService: GameService

    @InjectMocks
    lateinit var gameplayService: GameplayService

    private val gameId: UUID = UUID.randomUUID()
    private lateinit var clientMove: ClientMove
    private lateinit var game: IGame
    private lateinit var move: MoveResult

    @BeforeEach
    fun mockMethods() {
        game = mock()
        clientMove = mock()
        move = mock()
    }

    @Test
    fun `successfully makes move`() {
        whenever(gameService.get(gameId)).thenReturn(game)
        whenever(game.move(clientMove)).thenReturn(move)
        assert(gameplayService.makeMove(gameId, clientMove) == move)
        verify(game).move(clientMove)
    }

    @Test
    fun `throws error if game is not accepting moves`() {
        whenever(gameService.get(gameId)).thenReturn(game)
        whenever(game.move(clientMove)).thenThrow(UnsupportedOperationException::class.java)
        assertThrows<UnsupportedOperationException> {
            gameplayService.makeMove(gameId, clientMove)
        }
    }

    @Test
    fun `throws error if author in invalid`() {
        whenever(gameService.get(gameId)).thenReturn(game)
        whenever(game.move(clientMove)).thenThrow(InvalidAuthorException::class.java)
        assertThrows<InvalidAuthorException> {
            gameplayService.makeMove(gameId, clientMove)
        }
    }

    @Test
    fun `throws error if move is in invalid`() {
        whenever(gameService.get(gameId)).thenReturn(game)
        whenever(game.move(clientMove)).thenThrow(NullPieceException::class.java)
        assertThrows<NullPieceException> {
            gameplayService.makeMove(gameId, clientMove)
        }
    }

    @Test
    fun `throws no such element exception if game does not exist`() {
        whenever(gameService.get(gameId)).thenThrow(NoSuchElementException::class.java)

        assertThrows<NoSuchElementException> {
            gameplayService.makeMove(gameId, clientMove)
        }
    }
}