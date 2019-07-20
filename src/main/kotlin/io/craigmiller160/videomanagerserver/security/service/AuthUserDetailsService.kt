package io.craigmiller160.videomanagerserver.security.service

import io.craigmiller160.videomanagerserver.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

// TODO needs tests

@Service
class AuthUserDetailsService (
        private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUserName(username ?: "")
        return user?.let {
            org.springframework.security.core.userdetails.User(
                    user.userName, user.password, listOf()
            )
        } ?: throw UsernameNotFoundException("Unable to find user with username: $username")
    }

}