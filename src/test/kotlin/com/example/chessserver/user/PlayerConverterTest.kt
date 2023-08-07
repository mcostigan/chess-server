package com.example.chessserver.user

import com.example.chessserver.waitingRoom.Experience
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
internal class PlayerConverterTest {

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var playerConverter: PlayerConverter

    private val newPlayer = NewPlayer("user", "pass")


    @Test
    fun `should transfer attributes, encode password, and set experience to novice`() {
        whenever(passwordEncoder.encode("pass")).thenReturn("encoded-pass")
        val user = playerConverter.convert(newPlayer)

        assert(user.id != null)
        assert(user.name == newPlayer.name)
        assert(user.experience == Experience.NOVICE)
        assert(user.password == "encoded-pass")

        verify(passwordEncoder).encode("pass")

    }
}