package io.craigmiller160.videomanagerserver.jwt

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.service.security.VideoUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

// TODO needs tests

@Component
class JwtTokenProvider (
       private val tokenConfig: TokenConfig,
       private val videoUserDetailsService: VideoUserDetailsService
) {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    private var secretKey: String = ""

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(tokenConfig.key.toByteArray())
    }

    fun createToken(username: String): String {
        // TODO actually create JWT here
        return Base64.getEncoder().encodeToString(username.toByteArray())
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader(AUTHORIZATION_HEADER)
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.replace(Regex("^$BEARER_PREFIX"), "")
        }
        return null
    }

    fun validateToken(token: String): Boolean {
        // TODO do actual JWT validation here
        val username = getUsername(token)
        return "craig" == username
    }

    fun getUsername(token: String): String {
        // TODO actually get the username from the JWT
        val decoded = Base64.getDecoder().decode(token)
        return String(decoded)
    }

    fun getAuthentication(token: String): Authentication {
        // TODO actually build authentication here
        val userDetails = videoUserDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }


    // TODO finish implementing the parts that I need here

}