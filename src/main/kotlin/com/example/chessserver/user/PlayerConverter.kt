package com.example.chessserver.user

import com.example.chessserver.waitingRoom.Experience
import org.springframework.core.convert.converter.Converter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlayerConverter(private val passwordEncoder: PasswordEncoder) : Converter<NewPlayer, Player> {
    override fun convert(source: NewPlayer): Player =
        Player().apply {
            id = UUID.randomUUID()
            name = source.name
            password = passwordEncoder.encode(source.password)
            photo = source.photo
            experience = Experience.NOVICE
        }
}