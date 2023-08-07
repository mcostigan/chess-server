package com.example.chessserver.socket

import com.example.chessserver.auth.JwtService
import com.example.chessserver.auth.UnauthorizedException
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(private val jwtService: JwtService) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app","/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chess-websocket").setAllowedOrigins("*")
            .setHandshakeHandler(CustomHandshakeHandler(jwtService))
        registry.addEndpoint("/chess-websocket").setAllowedOrigins("*").withSockJS()
    }
}

class CustomHandshakeHandler(private val jwtService: JwtService) : DefaultHandshakeHandler() {
    // Custom class for storing principal
    override fun determineUser(
        request: ServerHttpRequest,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Principal {

        val bearerToken = getJwtFromCookies(request.headers["cookie"]?.first()) ?: throw UnauthorizedException()
        if (!jwtService.verifyToken(bearerToken)) {
            throw UnauthorizedException()
        }
        val userId = jwtService.getUserId(bearerToken)
        return SocketPrincipal(userId.toString())
    }

    private fun getJwtFromCookies(cookies: String?): String? {
        return cookies?.split("; ")?.find { it.startsWith("CHESS_ACCESS_TOKEN=") }?.split("=")?.last()
    }
}

class SocketPrincipal(private val name: String) : Principal {
    override fun getName(): String = this.name

}