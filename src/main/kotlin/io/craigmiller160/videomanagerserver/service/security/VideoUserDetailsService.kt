package io.craigmiller160.videomanagerserver.service.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

// TODO needs tests

@Service
class VideoUserDetailsService : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        // TODO actually implement lookup

        username.let {
            if ("craig" == username) {
                return User.withUsername(username)
                        .build()
            }
        }

        throw UsernameNotFoundException("Unable to find user with name: $username")
    }
}