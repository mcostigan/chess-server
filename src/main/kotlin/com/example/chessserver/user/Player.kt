package com.example.chessserver.user

import com.example.chessserver.waitingRoom.Experience
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

interface IPlayer {
    val id: UUID?
    val name: String
    val experience: Experience
}

@Entity
@Table(name = "players")
class Player

    : IPlayer {
    @Id
    override lateinit var id: UUID
    override lateinit var name: String

    override lateinit var experience: Experience

    @JsonIgnore
    lateinit var password: String

    lateinit var photo: String
}

data class NewPlayer(val name: String, val password: String){
    val photo: String = "https://picsum.photos/seed/${Random().nextInt(500)}/200/200"
}