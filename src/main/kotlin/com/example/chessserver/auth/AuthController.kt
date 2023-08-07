package com.example.chessserver.auth

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@CrossOrigin
class AuthController(private val authService: AuthService) {
    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest) = authService.login(authRequest)

}