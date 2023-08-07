package com.example.chessserver.auth

import com.example.chessserver.user.PlayerService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.HttpClientErrorException
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class AuthServiceTest {

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var playerService: PlayerService

    @Mock
    lateinit var jwtService: JwtService

    @InjectMocks
    lateinit var authService: AuthService

    val authRequest = AuthRequest("user", "pass")
    val mockUser: AuthProjection = mock()

    @Test
    fun `should issue a token on good credentials`() {
        whenever(mockUser.password).thenReturn("encoded-pass")
        whenever(mockUser.id).thenReturn(UUID.randomUUID())
        whenever(playerService.getByName("user", AuthProjection::class.java)).thenReturn(Optional.of(mockUser))
        whenever(passwordEncoder.matches("pass", "encoded-pass")).thenReturn(true)
        whenever(jwtService.generate(mockUser.id)).thenReturn("token")

        assert(authService.login(authRequest).token == "token")
        verify(passwordEncoder).matches("pass", "encoded-pass")
        verify(playerService).getByName("user", AuthProjection::class.java)
        verify(jwtService).generate(mockUser.id)
    }

    @Test
    fun `should throw unauthorized with bad credentials`() {
        whenever(playerService.getByName("user", AuthProjection::class.java)).thenReturn(Optional.of(mockUser))
        whenever(passwordEncoder.matches("pass", "encoded-pass")).thenReturn(false)
        whenever(mockUser.password).thenReturn("encoded-pass")

        assertThrows<UnauthorizedException> {
            authService.login(authRequest)
        }
        verify(jwtService, never()).generate(any())
    }

    @Test
    fun `should throw unauthorized if user does not exist`() {
        whenever(playerService.getByName("user", AuthProjection::class.java)).thenReturn(Optional.empty())
        assertThrows<UnauthorizedException> {
            authService.login(authRequest)
        }
        verify(jwtService, never()).generate(any())
        verify(passwordEncoder, never()).matches(any(), any())
    }

}