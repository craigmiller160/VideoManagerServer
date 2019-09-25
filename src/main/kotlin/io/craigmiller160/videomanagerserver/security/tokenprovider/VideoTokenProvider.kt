package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

// TODO need unit tests for all methods here

@Component
class VideoTokenProvider (
        private val tokenConfig: TokenConfig
) : TokenProvider {

    override fun createToken(user: AppUser, params: Map<String,Any>): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateToken(token: String): TokenValidationStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createAuthentication(token: String): Authentication {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getClaims(token: String): Map<String, Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRefreshAllowed(user: AppUser): Boolean {
        return false
    }
}