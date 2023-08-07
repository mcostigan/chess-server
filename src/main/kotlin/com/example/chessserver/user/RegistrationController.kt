package com.example.chessserver.user

import com.example.chessserver.auth.AuthResponse
import com.example.chessserver.auth.JwtService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class RegistrationController(private val playerService: PlayerService, private val jwtService: JwtService) {

    @PostMapping("/register")
    fun createUser(@RequestBody newPlayer: NewPlayer): AuthResponse =
        AuthResponse(jwtService.generate(playerService.register(newPlayer).id))
}