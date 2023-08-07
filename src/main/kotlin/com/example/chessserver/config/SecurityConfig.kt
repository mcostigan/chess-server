package com.example.chessserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Value("\${security.jwt.secret}")
    lateinit var jwtSecret: String

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun configure(http: HttpSecurity?): SecurityFilterChain {
        return http!!
            .csrf { it.disable() }
            .cors {}
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests { auth ->
                auth.anyRequest().permitAll()
                    .and()
                    .oauth2ResourceServer()
                    .bearerTokenResolver {
                        DefaultBearerTokenResolver().resolve(it)
                    }
                    .jwt().jwtAuthenticationConverter(TokenConverter())
            }
            .build()


    }

    @Bean
    fun jwtDecoder(): JwtDecoder? {
        return NimbusJwtDecoder.withSecretKey(SecretKeySpec(jwtSecret.toByteArray(), "HmacSHA256")).build()
    }

    class TokenConverter : Converter<Jwt, AbstractAuthenticationToken> {
        override fun convert(source: Jwt): AbstractAuthenticationToken {
            val user = getUser(jwt = source)
            return UsernamePasswordAuthenticationToken(user, "", user.grantedAuthorities)
        }

        private fun getUser(jwt: Jwt) = AuthenticatedUser(getUserId(jwt), getEmail(jwt), getAuthorities(jwt))

        private fun getUserId(jwt: Jwt): UUID {
            return UUID.fromString(jwt.claims["sub"] as String)
        }

        private fun getEmail(jwt: Jwt): String {
            return ""
        }

        private fun getAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
            return listOf<GrantedAuthority>()
        }
    }
}

data class AuthenticatedUser(
    val userId: UUID,
    val email: String,
    val grantedAuthorities: Collection<GrantedAuthority>
) :
    User(userId.toString(), "", grantedAuthorities)