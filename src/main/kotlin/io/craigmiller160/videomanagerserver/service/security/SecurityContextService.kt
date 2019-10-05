package io.craigmiller160.videomanagerserver.service.security

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class SecurityContextService {

    fun getSecurityContext(): SecurityContext = SecurityContextHolder.getContext()

    fun getUserName(): String {
        val principal = getSecurityContext().authentication.principal
        if (principal is UserDetails) {
            return principal.username
        }

        return principal.toString()
    }

}