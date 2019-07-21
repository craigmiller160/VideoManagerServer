package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService (
        private val userRepository: UserRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val jwtTokenProvider: JwtTokenProvider
) {

    fun login(request: User): Token {
        val user = userRepository.findByUserName(request.userName)
        if (user != null && passwordEncoder.matches(request.password, user.password)) {
            val token = jwtTokenProvider.createToken(user)
            return Token(token)
        }
        throw ApiUnauthorizedException("Invalid login")
    }

}