package com.example.chessserver.auth

import arrow.core.getOrElse
import io.github.nefilim.kjwt.JWSHMAC256Algorithm
import io.github.nefilim.kjwt.JWT
import io.github.nefilim.kjwt.sign
import io.github.nefilim.kjwt.verifySignature
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class JwtService {
    @Value("\${security.jwt.secret}")
    lateinit var jwtSecret: String

    @Value("\${security.jwt.duration}")
    private lateinit var duration: Duration

    fun generate(id: UUID): String {
        val iat = Instant.now()
        val exp = iat.plus(duration)

        val jwt = JWT.hs256 {
            subject(id.toString())
            issuer("chess-server")
            issuedAt(LocalDateTime.ofInstant(iat, ZoneId.of("UTC")))
            expiresAt(LocalDateTime.ofInstant(exp, ZoneId.of("UTC")))
        }

        return jwt.sign(jwtSecret).fold({ throw BadTokenException() }, { right -> right.rendered })

    }

    fun verifyToken(jwt: String): Boolean {
        return verifySignature<JWSHMAC256Algorithm>(jwt, jwtSecret).fold({ false }, { true })
    }

    fun getUserId(jwt: String): UUID {
        return UUID.fromString(
            JWT.decode(jwt).getOrElse { throw UnauthorizedException() }.subject()
                .getOrElse { throw UnauthorizedException() })
    }
}

class BadTokenException: java.lang.Exception(){

}


