package com.example.chessserver.user

import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerService(private val playerConverter: PlayerConverter, private val playerRepo: PlayerRepo) {
    fun register(newPlayer: NewPlayer): Player {
        val user = playerConverter.convert(newPlayer)
        return playerRepo.save(user)
    }

    fun getPlayer(id: UUID): IPlayer = playerRepo.findById(id).orElseThrow()
    fun <T> getByName(name: String, projection: Class<T>): Optional<T> = playerRepo.getPlayerByName(name, projection)
}


