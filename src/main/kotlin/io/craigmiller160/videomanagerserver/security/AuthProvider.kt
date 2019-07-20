package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.repository.UserRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

// TODO needs tests

@Component
class AuthProvider (
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {

    override fun authenticate(auth: Authentication?): Authentication {
        TODO("Finish this")
    }

    override fun supports(clazz: Class<*>?): Boolean =
            clazz == UsernamePasswordAuthenticationToken::class.java

}