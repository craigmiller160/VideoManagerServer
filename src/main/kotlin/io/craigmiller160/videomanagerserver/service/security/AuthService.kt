package io.craigmiller160.videomanagerserver.service.security

import io.craigmiller160.videomanagerserver.dto.Token
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

// TODO needs tests

@Service
class AuthService (
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {

    fun login(request: User): Token {
        val password = passwordEncoder.encode(request.password)
        val user = userRepository.login(request.userName, password)
        user.password = ""
        TODO("Finish this")
    }

}