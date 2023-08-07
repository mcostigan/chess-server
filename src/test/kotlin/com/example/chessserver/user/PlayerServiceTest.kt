package com.example.chessserver.user

import com.example.chessserver.waitingRoom.Experience
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class PlayerServiceTest {
    @Mock
    lateinit var playerConverter: PlayerConverter

    @Mock
    lateinit var playerRepo: PlayerRepo

    @InjectMocks
    lateinit var playerService: PlayerService


    @Test
    fun register() {
        val newPlayer = NewPlayer("user", "pass")
        val player = Player().apply {
            id = UUID.randomUUID()
            name = newPlayer.name
            password = "encoded-password"
            experience = Experience.NOVICE
        }

        whenever(playerConverter.convert(newPlayer)).thenReturn(player)
        whenever(playerRepo.save(player)).thenReturn(player)

        val result = playerService.register(newPlayer)
        assert(result.id == player.id)
        assert(result.name == player.name)
        assert(result.experience == player.experience)
        verify(playerConverter).convert(newPlayer)
        verify(playerRepo).save(player)
    }
}