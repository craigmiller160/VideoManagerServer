package io.craigmiller160.videomanagerserver.security

import org.springframework.security.core.GrantedAuthority

data class AuthGrantedAuthority (
        private val authorityString: String
) : GrantedAuthority {

    override fun getAuthority() = authorityString
}