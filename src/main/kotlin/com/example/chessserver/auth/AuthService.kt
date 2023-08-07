package com.example.chessserver.auth

import com.example.chessserver.user.PlayerService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val playerService: PlayerService,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(authRequest: AuthRequest): AuthResponse {
        try {
            val player = playerService.getByName(authRequest.name, AuthProjection::class.java).orElseThrow()
            if (!passwordEncoder.matches(authRequest.password, player.password)) {
                throw UnauthorizedException()
            }
            return AuthResponse(jwtService.generate(player.id))
        } catch (e: Exception) {
            throw UnauthorizedException()
        }
    }
}

class AuthRequest(val name: String, val password: String)

class AuthResponse(val token: String)

interface AuthProjection {
    val id: UUID
    val name: String
    val password: String
}
