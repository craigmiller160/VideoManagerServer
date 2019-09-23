package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.dto.AppUser
import org.springframework.security.core.Authentication
import javax.servlet.http.HttpServletRequest

interface TokenProvider<T> {

    fun createToken(user: AppUser): String

    fun resolveToken(req: HttpServletRequest): String?

    fun validateToken(token: String): TokenValidationStatus

    fun createAuthentication(token: String): Authentication

    fun getClaims(token: String): T

    fun isRefreshAllowed(user: AppUser): Boolean

}