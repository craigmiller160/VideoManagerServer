package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.exception.ApiUnauthorizedException
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.repository.AppUserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService (
        private val appUserRepository: AppUserRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val jwtTokenProvider: JwtTokenProvider
) {

    fun login(request: AppUser): Token {
        val user = appUserRepository.findByUserName(request.userName)
        if (user != null && passwordEncoder.matches(request.password, user.password)) {
            val token = jwtTokenProvider.createToken(user)
            return Token(token)
        }
        throw ApiUnauthorizedException("Invalid login")
    }

}