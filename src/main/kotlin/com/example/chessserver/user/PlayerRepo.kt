package com.example.chessserver.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlayerRepo : JpaRepository<Player, UUID> {

    fun <T> getPlayerByName(name: String, projection: Class<T>): Optional<T>
}